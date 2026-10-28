package yzx.sacommon.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import yzx.sacommon.domain.dto.CurrentUser;
import yzx.sacommon.domain.dto.DataScopeRule;
import yzx.sacommon.domain.entity.*;
import yzx.sacommon.enums.DataScopeEnum;
import yzx.sacommon.mapper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: DataScopeResolver
 * @author: yzx
 * @date: 2026/10/27 16:00
 * @Version: 1.0
 * @description:
 */
@Component
public class DataScopeResolver {
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final SysDeptMapper deptMapper;
    private final SysRoleDeptMapper roleDeptMapper;

    public DataScopeResolver(SysUserMapper userMapper,
                             SysUserRoleMapper userRoleMapper,
                             SysRoleMapper roleMapper,
                             SysDeptMapper deptMapper,
                             SysRoleDeptMapper roleDeptMapper) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.deptMapper = deptMapper;
        this.roleDeptMapper = roleDeptMapper;
    }

    public CurrentUser resolve(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        currentUser.setDeptId(user.getDeptId());
        currentUser.setUserType(user.getUserType());
        currentUser.setTenantId(user.getTenantId());
        if ("00".equals(user.getUserType())) {
            return currentUser;
        }
        //找到用户的角色ids
        List<Long> rolesIds = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().
                        eq(SysUserRole::getUserId, userId)).
                stream().
                map(SysUserRole::getRoleId).collect(Collectors.toList());
                if(rolesIds.isEmpty()){
                    currentUser.setRules(List.of());
                    return currentUser;
                }
        //找到用户的角色
        List<SysRole> roles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId, rolesIds)
                .eq(SysRole::getStatus, "0"));
        List<DataScopeRule> rules = new ArrayList<>();
        for (SysRole role : roles) {
            DataScopeEnum scope = DataScopeEnum.of(role.getDataScope());
            if (scope == null) {
                continue;
            }
            List<Long> deptIds = null;
            switch (scope) {
                case DEPT -> deptIds = user.getDeptId() == null ? List.of() : List.of(user.getDeptId());
                case DEPT_AND_CHILD ->deptIds= selectDeptAndChildren(user.getDeptId());
                case CUSTOM -> deptIds= roleDeptMapper.selectList(new LambdaQueryWrapper<SysRoleDept>()
                                .eq(SysRoleDept::getRoleId, role.getId())
                        ).stream()
                        .map(SysRoleDept::getDeptId)
                        .collect(Collectors.toList());
                default -> {
                    break;
                }
            }
            rules.add(new DataScopeRule(role.getDataScope(), deptIds));
        }
        currentUser.setRules(rules);
        return currentUser;
    }

    private List<Long> selectDeptAndChildren(Long deptId) {
        if (deptId == null) {
            return List.of();
        }
        return deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                        .and(w -> w.eq(SysDept::getId, deptId).or().apply("FIND_IN_SET({0},ancestors)", deptId)))
                .stream()
                .map(SysDept::getId)
                .toList();
    }
}
