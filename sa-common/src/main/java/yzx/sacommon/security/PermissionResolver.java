package yzx.sacommon.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import yzx.sacommon.domain.dto.PermissionSnapshot;
import yzx.sacommon.domain.entity.SysMenu;
import yzx.sacommon.domain.entity.SysRole;
import yzx.sacommon.domain.entity.SysRoleMenu;
import yzx.sacommon.domain.entity.SysUserRole;
import yzx.sacommon.mapper.SysMenuMapper;
import yzx.sacommon.mapper.SysRoleMapper;
import yzx.sacommon.mapper.SysRoleMenuMapper;
import yzx.sacommon.mapper.SysUserRoleMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        //根据用户的id获取角色roIds
        List<Long> roleIds = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return new PermissionSnapshot(List.of(), List.of());
        }
        //根据角色的id获取角色
        List<SysRole> roles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId, roleIds)
                .eq(SysRole::getStatus, "0")
        );

        if (roles.isEmpty()) {
            return new PermissionSnapshot(List.of(), List.of());
        }
        //去重
        List<String> roleKeys = roles.stream()
                .map(SysRole::getRoleKey)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        //去重后获取角色的id
        List<Long> activeRoleIds = roles.stream().map(SysRole::getId).toList();
        //根据角色的id获取menu
        List<Long> menuIds = roleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                        .in(SysRoleMenu::getRoleId, activeRoleIds))
                .stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());
        if (menuIds.isEmpty()) {
            return new PermissionSnapshot(List.of(), List.of());
        }
        //根据menu遍历出perms
        List<String> permissions = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                        .in(SysMenu::getId, menuIds)
                        .eq(SysMenu::getStatus, "0")
                ).stream()
                .map(SysMenu::getPerms)
                .filter(perms -> perms != null && perms.isBlank())
                .distinct()
                .toList();
        return new PermissionSnapshot(permissions, roleKeys
        );
    }

}
