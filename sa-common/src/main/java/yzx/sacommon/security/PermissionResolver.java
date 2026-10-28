package yzx.sacommon.security;

import org.springframework.stereotype.Component;
import yzx.sacommon.domain.dto.PermissionSnapshot;
import yzx.sacommon.mapper.SysMenuMapper;
import yzx.sacommon.mapper.SysRoleMapper;
import yzx.sacommon.mapper.SysRoleMenuMapper;
import yzx.sacommon.mapper.SysUserRoleMapper;

/**
 * @className: PermissionResolver
 * @author: yzx
 * @date: 2026/10/28 11:55
 * @Version: 1.0
 * @description:
 */
@Component
public class PermissionResolver {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;

    public PermissionResolver(SysUserRoleMapper userRoleMapper,
                              SysRoleMapper roleMapper,
                              SysRoleMenuMapper roleMenuMapper,
                              SysMenuMapper menuMapper) {
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.menuMapper = menuMapper;
    }

    public PermissionSnapshot resolve(Long userId) {

    }
}
