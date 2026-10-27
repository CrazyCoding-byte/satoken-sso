package yzx.sacommon.security;

import yzx.sacommon.domain.dto.CurrentUser;

/**
 * 当前用户上下文
 * <p>
 * 一次请求一个线程，所以用 ThreadLocal。
 * 请求结束必须调用 clear()，否则线程池复用会把上一个用户的数据权限带到下一个请求。
 */
public final class CurrentUserHolder {

    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    private CurrentUserHolder() {
    }

    public static void set(CurrentUser user) {
        HOLDER.set(user);
    }

    public static CurrentUser get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
