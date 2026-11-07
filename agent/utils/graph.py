from langgraph.graph import END, MessagesState, StateGraph
from langchain_ollama import ChatOllama
from langgraph.prebuilt import ToolNode, tools_condition

from main import model_with_tools
from utils.tools import TOOLS

model=ChatOllama(model="qwen3:8b",temperature=0)
model_with_tools=model.bind_tools(TOOLS)
async def agent_node(state:MessagesState):
    ai=await model_with_tools.ainvoke()