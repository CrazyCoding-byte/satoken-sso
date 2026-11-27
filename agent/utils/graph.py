from langgraph.graph import END, MessagesState, StateGraph
from langchain_core.messages import HumanMessage, SystemMessage, trim_messages
from langchain_ollama import ChatOllama
import aiosqlite
from langgraph.checkpoint.sqlite.aio import AsyncSqliteSaver
from langgraph.store.sqlite.aio import AsyncSqliteStore
from langgraph.prebuilt import ToolNode, tools_condition
from utils.tools import TOOLS

graph = None
store = None
model = ChatOllama(model="qwen3:8b",
                   temperature=0,
                   keep_alive="30m",  # 30 分钟内不卸载模型，避免冷启动撞上请求
                   num_ctx=8192,  # 默认 4096 太小，订单 JSON 一长就截断
                   )
model_with_tools = model.bind_tools(TOOLS)

MAX_CONTEXT_TOKENS = 3000  # 只把最近 8 条消息喂给模型,state 里仍然存全量


# def trim_messages(messages: list) -> list:
#     """保留开头的 system,再加最近 MAX_HISTORY 条;从第一条 HumanMessage 开始,避免切断 tool_calls 和 ToolMessage 的配对"""
#     # 文件len没超过8就返回
#     if len(messages) <= MAX_HISTORY:
#         return messages
#     #	只留第一条 system，后续单独拼回去 if isinstance 类似于java的
#     system = [m for m in messages if isinstance(m, SystemMessage)][:1]
#     tail = messages[-MAX_HISTORY:]  # 最后 8 条（负索引 = Java 的 subList(size-8, size)）
#     start = 0
#     """
#         while (start < tail.size() && !(tail.get(start) instanceof HumanMessage)) {
#             start++;
#         }
#     """
#     while start < len(tail) and not isinstance(tail[start], HumanMessage):  # 丢掉尾部开头的零碎 AI/Tool 消息
#         start += 1
#     if start == len(tail):
#         return messages
#     return system + tail[start:]

def build_context(messages: list) -> list:
    return trim_messages(
        messages,
        max_tokens=MAX_CONTEXT_TOKENS,
        token_counter="approximate",
        strategy="last",
        allow_partial=False,
        start_on="human",
        include_system=True
    )


"""
这里这个messages对象是一个dict 存储的当前和ai的聊天信息
state = {
    "messages": [
        SystemMessage(content="你是订单系统的助手。需要订单或用户数据时必须调用工具查询……"),
        HumanMessage(content="我叫张三，请记住"),
        AIMessage(content="好的，我记住了您的名字是张三。"),
        HumanMessage(content="我叫什么名字？"),
        AIMessage(content="", tool_calls=[{"name": "current_user", "args": {}, "id": "call_abc"}]),
        ToolMessage(content="查询失败:ConnectError ...", tool_call_id="call_abc"),
        AIMessage(content="您的名字是张三。当前系统无法获取登录信息。"),
    ]
}  类似于这样
"""

"""
   ┌───────── set_entry_point ─────────┐
        ↓                                   │
      agent ──tools_condition──> tools ─────┘ (add_edge)
        │
        └──tools_condition──> END
"""
async def agent_node(state: MessagesState):
    msgs = build_context(state["messages"])
    items = await store.asearch(("memories",))
    if items:
        facts = ": ".join(f"{i.key}={i.value['text']}" for i in items)
        system = [m for m in msgs if isinstance(m, SystemMessage)]
        others = [m for m in msgs if not isinstance(m, SystemMessage)]
        msgs = system + [SystemMessage(content=f"关于这个用户的已知信息:{facts}")] + others
    ai = await model_with_tools.ainvoke(msgs)
    return {"messages": [ai]}


builder = StateGraph(MessagesState)  # 一张空流程图,状态里就一个 message
builder.add_node("agent", agent_node)  # 节点1: 问模型
builder.add_node("tools", ToolNode(TOOLS))  # 节点2:执行工具
builder.set_entry_point("agent")  # 从哪开始
builder.add_conditional_edges("agent", tools_condition)  # 模型要工具->tools,不要->结束
builder.add_edge("tools", "agent")  # 工具跑完->再问模型


async def init_graph() -> None:
    global graph, store
    conn = await aiosqlite.connect("checkpoints.db")
    store_conn = await aiosqlite.connect("store.db", isolation_level=None)
    store = AsyncSqliteStore(store_conn)  #
    await store.setup()  # 等价于建表，不调用第一次`aput` 会报表不存在。
    graph = builder.compile(checkpointer=AsyncSqliteSaver(conn), store=store)
