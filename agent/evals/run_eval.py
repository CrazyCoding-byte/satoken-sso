import asyncio
import sys
import uuid
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))
from langchain_core.messages import HumanMessage, SystemMessage
import main
import utils.graph as graph_module
from utils.rag import build_index

CASES = [
    {"q": "删除订单有什么限制？", "must_all": ["待处理"], "not": ["物理删除"]},
    {"q": "订单号怎么生成的？", "must_all": ["ORD", "流水"], "not": []},
    {"q": "订单总额怎么算？", "must_all": ["明细"], "not": ["运费计入"]},
    {"q": "订单多久不支付会被取消？", "must_all": ["30"], "not": []},
    {"q": "部门管理员能看到哪些订单？", "must_all": ["本部门"], "not": []},
    {"q": "怎么申请发票？", "must_all": ["发票"], "not": ["未提及", "未在知识库", "没有找到"]},
    {"q": "订单可以修改吗？", "must_all": ["待处理"], "not": []},
    {"q": "退款多久到账？", "must_all": ["退款"], "not": ["没有关于", "未提及", "未在知识库", "没有明确"]},
    {"q": "公司食堂几点开门？", "must_any": ["无法", "不知道", "没有"], "not": []},
    {"q": "我昨天吃了什么？", "must_any": ["无法", "不知道", "没有", "暂无"], "not": []},
]


def judge(reply: str, case: dict) -> tuple[bool, str]:
    text = reply.replace(" ", "").lower()
    for kw in case.get("not", []):
        if kw.replace(" ", " ").lower() in text:
            return False, f"出现禁用词:{kw}"
    for kw in case.get("must_all", []):
        if kw.replace(" ", " ").lower() not in text:
            return False, f"缺少关键词:{kw}"
    any_list = case.get("must_any", [])
    if any_list and not any(kw.replace(" ", " ").lower() in text for kw in any_list):
        return False, f"缺少任一关键词:{any_list}"
    return True, "ok"


async def ask(question: str) -> str:
    out = await graph_module.graph.ainvoke(
        {
            "messages": [
                SystemMessage(content=main.SYSTEM_PROMPT),
                HumanMessage(content=question)
            ]
        },
        {"configurable": {"thread_id": f"eval-{uuid.uuid4().hex[:8]}"}, "recursion_limit": 8},
        version="v2",
    )
    return out.value["messages"][-1].text


async def run() -> None:
    await graph_module.init_graph()
    print("知识库片段数:", build_index())
    passed = 0
    for case in CASES:
        reply = await ask(case["q"])
        ok, why = judge(reply, case)
        passed += ok
        print(f"[{'PASS' if ok else 'FAIL'}]{case['q']} ({why})")
        print(f"    {reply[:90]}")
    print(f"\n通过 {passed}/{len(CASES)} = {passed / len(CASES):.0%}")


if __name__ == "__main__":
    asyncio.run(run())
