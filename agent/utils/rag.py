from pathlib import Path
from langchain_core.vectorstores import InMemoryVectorStore
from langchain_ollama import OllamaEmbeddings

KNOWLEDGE_FILE = Path(__file__).parent.parent / "knowledge" / "orders.md"
embeddings = OllamaEmbeddings(model="nomic-embed-text")
vectorstore: InMemoryVectorStore | None = None


def build_index() -> int:
    """读文档->按空行切块->向量化->放进内存向量库"""
    global vectorstore
    text = KNOWLEDGE_FILE.read_text(encoding="utf-8")
    chunks = [c.strip() for c in text.split("\n\n") if c.strip()]
    vectorstore = InMemoryVectorStore.from_texts(chunks, embeddings)
    return len(chunks)


async def search(query: str, k: int = 3) -> str:
    if vectorstore is None:
        return "数据还未加载"
    docs = await vectorstore.asimilarity_search(query, k=k)
    return "\n----\n".join(d.page_conten for d in docs)
