import uuid
from contextlib import asynccontextmanager
from fastapi import FastAPI, Header
from langchain_core.messages import HumanMessage, SystemMessage
from langgraph.types import Command
from pydantic import BaseModel, Field
from utils.rag import build_index
import utils.graph as graph_module  # import 直接引入整个模块 from引入特定的类
from utils.tools import bind_token, reset_token


@asynccontextmanager
async def lifespan(app: FastAPI):
    await graph_module.init_graph()
    print("知识库片段数:", build_index())
    yield


app = FastAPI(title="sa-agent", lifespan=lifespan)

SYSTEM_PROMPT = (
    "你是订单系统的助手。需要订单或用户数据时必须调用工具查询，不要编造。"
    "转述工具结果时不得改变结论：工具说操作未执行，就不能说已执行。"
    "回答简洁，用中文，控制在五句话以内。"
)


class ChatRequest(BaseModel):
    message: str = Field(min_length=1, max_length=2000)
    thread_id: str | None = None


class ConfirmRequest(BaseModel):
    thread_id: str
    decision: str = "reject"


def make_config(thread_id: str) -> dict:
    return {"configurable": {"thread_id": thread_id}, "recursion_limit": 8}


async def build_input(thread_id: str, message: str) -> dict:
    """新线程才带 system；老线程只追加用户这句"""
    state = await graph_module.graph.aget_state(make_config(thread_id))
    history = (state.values or {}).get("messages", [])
    messages = [HumanMessage(content=message)]
    if not history:
        messages.insert(0, SystemMessage(content=SYSTEM_PROMPT))
    return {"messages": messages}


@app.get("/")
async def root():
    return {"service": "sa-agent", "docs": "/docs"}


@app.post("/chat")
async def chat(req: ChatRequest, satoken: str | None = Header(default=None)):
    handle = bind_token(satoken)
    thread_id = req.thread_id or str(uuid.uuid4())
    try:
        payload = await build_input(thread_id, req.message)
        out = await graph_module.graph.ainvoke(payload, config=make_config(thread_id), version="v2")
        if out.interrupts:
            return {
                "status": "pending_approval",
                "thread_id": thread_id,
                "question": out.interrupts[0].value,
            }
        return {
            "status": "done",
            "thread_id": thread_id,
            "reply": out.value["messages"][-1].text
        }
    finally:
        reset_token(handle)


@app.post("/chat/confirm")
async def confirm(req: ConfirmRequest, satoken: str | None = Header(default=None)):
    handle = bind_token(satoken)
    try:
        out = await graph_module.graph.ainvoke(
            Command(resume=req.decision),
            config=make_config(req.thread_id),
            version="v2",
        )
        if out.interrupts:
            return {
                "status": "pending_approval",
                "thread_id": req.thread_id,
                "question": out.interrupts[0].value,
            }
        return {
            "status": "done",
            "thread_id": req.thread_id,
            "reply": out.value["messages"][-1].text,
        }
    finally:
        reset_token(handle)


@app.get("/chat/history/{thread_id}")
async def history(thread_id: str):
    """把checkpointer 里的消息摊开,方便确认记忆存在"""
    state = await graph_module.graph.aget_state(
        make_config(thread_id))  # 从checkpoint 读出这个thread的当前状态 repository.findById
    messages = (state.values or {}).get("messages", [])
    return {
        "thread_id": thread_id,
        "count": len(messages),
        "messages": [
            {"type": type(m).__name__, "content": str(m.content)[:300]}
            for m in messages
        ]
    }
