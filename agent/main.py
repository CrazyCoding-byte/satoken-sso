from fastapi import FastAPI, Header  ## fastapi springboot RestController
from pydantic import BaseModel, Field  # pydantic Dto+@Valid+@BotBlank 定义“请求长什么样”，不合格就 422
from langchain_core.messages import HumanMessage, SystemMessage, \
    ToolMessage  # ChatOllama(model="qwen3:8b") `` 相当于注入一个远程服务的 Client Bean
from langchain_ollama import ChatOllama  # langchain_ollama 相当于一个`RestTemplate` 的封装 LangChain 对接本地 Ollama 的适配包
from utils.tools import TOOLS, bind_token, reset_token

app = FastAPI(title="sa-agent")  # app = FastAPI() `SpringApplication` 启动出来的上下文 ：所有路由都挂在它身上
model = ChatOllama(model="qwen3:8b", temperature=0)
model_with_tools = model.bind_tools(TOOLS)
TOOLS_BY_NAME = {t.name: t for t in TOOLS}
SYSTEM_PROMPT = "你是一个简洁的中文助手,回答控制在三句话以内"


class ChatRequest(BaseModel):  # 继承BaseModel 类
    message: str = Field(min_length=1,
                         max_length=2000)  # `Field(min_length=1)` 等价于 Java 的`@Size(min = 1, max = 2000)` ： 空字符串、超长字符串会被在校验阶段拒掉，返回 422 。


@app.get("/")
async def root():
    return {"service": "sa-agent", "docs": "/docs"}


@app.post("/chat")
async def chat(req: ChatRequest, satoken: str | None = Header(default=None)):
    handle = bind_token(satoken)
    used_tools: list[str] = []
    try:
        messages = [
            SystemMessage(content=SYSTEM_PROMPT),  # 设定身份、规矩，每次请求都要带上 `` system
            HumanMessage(content=req.message)  # 用户这句话 `` user
        ]
        for _ in range(5):
            ai = await model_with_tools.ainvoke(messages)
            messages.append(ai)
            if not ai.tool_calls:
                return {"replay": ai.text, "used_tools": used_tools}
            for call in ai.tool_calls:  # 模型不再要工具,说明它准备回答了
                used_tools.append(call["name"])
                tool = TOOLS_BY_NAME[call["name"]]
                try:
                    result = await tool.ainvoke(call["args"])
                except Exception as exc:
                    result = f"工具执行异常:{exc}"
                messages.append(
                    ToolMessage(content=str(result), tool_call_id=call["id"])
                )
        return {"reply": "工具调用轮次过多,已停止", "used_tools": used_tools}
    finally:
        reset_token(handle)
