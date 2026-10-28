package yzx.sacommon.security;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @className: SaPermissionProvider
 * @author: yzx
 * @date: 2026/10/28 12:07
 * @Version: 1.0
 * @description:
 *
 *
 *  * Sa-Token 的权限数据源。
 *  * <p>
 *  * 只读会话，不查库 —— 这是"登录时算一次"的落点。
 *  * 放在 sa-common：sa-user、sa-model 各自的 @SaCheckPermission 都要用到它。
 *
 *
 */
public class SaPermissionProvider implements StpInterface {
    /** 与登录时写入会话的 key 必须完全一致 */
    public static final String PERMISSIONS_KEY = "PERMISSIONS";
    public static final String ROLES_KEY = "ROLES";

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of();
    }

    private List<String> read(Object loginId, String key) {
        Object value = StpUtil.getSessionByLoginId(loginId).get(key);
        if (value instanceof Collection<?> collection) {
            List<String> result = new ArrayList<>(collection.size());
            for (Object item : collection) {
                result.add(String.valueOf(item));
            }
            return result;
        }
        return List.of();
    }
}
