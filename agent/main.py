import uuid

from fastapi import FastAPI, Header
from langchain_core.messages import HumanMessage, SystemMessage
from langgraph.types import Command
from pydantic import BaseModel, Field

from utils.graph import graph
from utils.tools import bind_token, reset_token

app = FastAPI(title="sa-agent")

SYSTEM_PROMPT = (
    "你是订单系统的助手。需要订单或用户数据时必须调用工具查询，不要编造。"
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

@app.get("/")
async def root():
    return {"service": "sa-agent", "docs": "/docs"}

@app.post("/chat")
async def chat(req: ChatRequest, satoken: str | None = Header(default=None)):
    handle = bind_token(satoken)
    thread_id = req.thread_id or str(uuid.uuid4())
    try:
        out = await graph.ainvoke(
            {"messages": [
                SystemMessage(content=SYSTEM_PROMPT),
                HumanMessage(content=req.message),
            ]},
            config=make_config(thread_id),
            version="v2",
        )
        if out.interrupts:
            return {
                "status": "pending_approval",
                "thread_id": thread_id,
                "question": out.interrupts[0].value,
            }
        return {
            "status": "done",
            "thread_id": thread_id,
            "reply": out.value["messages"][-1].text,
        }
    finally:
        reset_token(handle)

@app.post("/chat/confirm")
async def confirm(req: ConfirmRequest, satoken: str | None = Header(default=None)):
    handle = bind_token(satoken)
    try:
        out = await graph.ainvoke(
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