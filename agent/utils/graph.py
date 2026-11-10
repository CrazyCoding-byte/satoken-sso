from langgraph.graph import END, MessagesState, StateGraph
from langchain_ollama import ChatOllama
from langgraph.prebuilt import ToolNode, tools_condition

from main import model_with_tools
from utils.tools import TOOLS

model = ChatOllama(model="qwen3:8b", temperature=0)
model_with_tools = model.bind_tools(TOOLS)


async def agent_node(state: MessagesState):
    ai = await model_with_tools.ainvoke()
    return {"messages": [ai]}


builder = StateGraph(MessagesState)  # 一张空流程图,状态里就一个 message
builder.add_node("agent", agent_node)  # 节点1: 问模型
builder.add_node("tools", ToolNode(TOOLS))  # 节点2:执行工具
builder.set_entry_point("agent")  # 从哪开始
builder.add_conditional_edges("agent", tools_condition)  # 模型要工具->tools,不要->结束
builder.add_edge("tools", "agent")  # 工具跑完->再问模型
graph = builder.compile()
