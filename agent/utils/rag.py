from pathlib import Path
from langchain_core.vectorstores import InMemoryVectorStore
from langchain_ollama import OllamaEmbeddings

KNOWLEDGE_FILE = Path(__file__).parent.parent / "knowledge" / "orders.md"
embeddings = OllamaEmbeddings(model="nomic-embed-text")
#List<Pair<String, double[]>>
vectorstore: InMemoryVectorStore | None = None


##存储索引 返回存的len
def build_index() -> int:
    """读文档->按空行切块->向量化->放进内存向量库"""
    global vectorstore
    text = KNOWLEDGE_FILE.read_text(encoding="utf-8")
    """
    先执行text.split("\n\n")
    for循环
    if c.strip() 
    c.strip()  这个c.strip()实际调用的是那道c.strip()返回的结果后放进的append()
    """
    chunks = [c.strip() for c in text.split("\n\n") if c.strip()]
    #对每个 chunk 调 embedding 模型，得到 768 维向量，然后把 (原文, 向量) 存进内存列表 "相当于 List<Pair<String, double[]>>，每项是""文本 + 向量"
    vectorstore = InMemoryVectorStore.from_texts(chunks, embeddings)
    #只返回条数，给日志用（知识库片段数: 9）
    return len(chunks)


async def search(query: str, k: int = 3) -> str:
    #守卫：没建索引就提前返回
    if vectorstore is None:
        return "数据还未加载"
    #把 query 向量化，算相似度，返回前 k 个 Document
    docs = await vectorstore.asimilarity_search(query, k=k)
    #把 3 段正文拼成一个大字符串 工具的返回值最终会变成一条`ToolMessage`
    #塞进对话上下文，模型只能读文本。所以这里把 3 个`Document` 拍平成一个字符串，用`\n----\n`
    # 分隔 —— 让模型能看出"这是 3 段独立内容"，而不是连成一句话。
    return "\n----\n".join(d.page_content for d in docs)
