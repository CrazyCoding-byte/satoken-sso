import json
from contextvars import ContextVar
from typing import Annotated
import httpx
from langchain_core.tools import tool
from pydantic import Field

ORDERS_API = "http://localhost:8083"
AUTH_API = "http://localhost:8081"

_token: ContextVar[str | None] = ContextVar("user_token", default=None)


def bind_token(token: str | None):
    return _token.set(token)


def reset_token(handle) -> None:
    _token.reset(handle)


async def _get_json(base: str, path: str, params: dict | None = None) -> tuple[bool, object]:
    headers = {"Accept": "application/json"}
    if _token.get():
        headers["satoken"] = _token.get()
    try:
        async with httpx.AsyncClient(timeout=15) as client:
            resp = await client.get(base + path, params=params, headers=headers)
        payload = resp.json()
    except httpx.HTTPError as exc:
        return False, f"请求 {base}{path} 失败：{exc.__class__.__name__} {exc}"
    except ValueError:
        return False, f"{base}{path}返回的不是JSON(HTTP{resp.status_code})"
    if isinstance(payload, dict) and "200" in payload:
        return True, payload["200"]  # 你后端Result的信封
    if isinstance(payload, dict) and "500" in payload:
        return False, payload["500"]
    return False, f"Http{resp.status_code}:{payload}"


@tool
async def query_orders(current: Annotated[int, Field(ge=1, description="页码,从1开始")] = 1,
                       size: Annotated[int, Field(ge=1, le=50, description="每页条数,1~50")] = 10,
                       ) -> str:
    """查询当前登录用户能看到的订单分页列表。后端会按租户/部门/角色改写 SQL，条数就是数据权限范围。"""
    ok, data = await _get_json(ORDERS_API, "/order/page", {"current": current, "size": size})
    if not ok:
        return f"查询订单失败：{data}"
    return json.dumps(data, ensure_ascii=False, default=str)[:3000]


@tool
async def current_user() -> str:
    """查询当前登录用户是谁、属于那个租户和部门、有那些权限标识"""
    ok, data = await _get_json(AUTH_API, "/auth/info")
    if not ok:
        return f"查询失败:{data}"
    return json.dump(data, ensure_ascii=False, default=str)


TOOLS = [query_orders, current_user]
