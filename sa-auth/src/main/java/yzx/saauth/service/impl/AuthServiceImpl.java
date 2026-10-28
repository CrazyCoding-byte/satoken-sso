package yzx.saauth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import yzx.saauth.dto.LoginRequest;
import yzx.saauth.dto.LoginResponse;
import yzx.saauth.service.AuthService;
import yzx.sacommon.domain.dto.CurrentUser;
import yzx.sacommon.domain.entity.SysTenant;
import yzx.sacommon.domain.entity.SysUser;
import yzx.sacommon.mapper.SysTenantMapper;
import yzx.sacommon.mapper.SysUserMapper;
import yzx.sacommon.security.DataScopeResolver;
/**
 * @className: AuthServiceImpl
 * @author: yzx
 * @date: 2026/10/28 10:11
 * @Version: 1.0
 * @description:
 */
@Service
public class AuthServiceImpl implements AuthService {
    public static final String SESSION_KEY = "CURRENT_USER";
    private final SysUserMapper sysUserMapper;
    private final SysTenantMapper tenantMapper;
    private final DataScopeResolver dataScopeResolver;
    private final ObjectMapper objectMapper;

    public AuthServiceImpl(SysUserMapper sysUserMapper, SysTenantMapper tenantMapper, DataScopeResolver dataScopeResolver, ObjectMapper objectMapper) {
        this.sysUserMapper = sysUserMapper;
        this.tenantMapper = tenantMapper;
        this.dataScopeResolver = dataScopeResolver;
        this.objectMapper = objectMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        //定位租户
        SysTenant tenant = tenantMapper.selectOne(new LambdaQueryWrapper<SysTenant>()
                .eq(SysTenant::getTenantCode, request.getTenantCode()));
        if (tenant == null || !"0".equals(tenant.getStatus())) {
            throw new IllegalArgumentException("租户不存在或已停用");
        }
        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getTenantId, tenant.getId())
                .eq(SysUser::getUsername, request.getUsername()));
        if (sysUser == null || !sysUser.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (!"0".equals(sysUser.getStatus())) {
            throw new IllegalArgumentException("账号已被停用");
        }

        //satoken 登录
        StpUtil.login(sysUser.getId());
        CurrentUser currentUser = dataScopeResolver.resolve(sysUser.getId());
        try {
            StpUtil.getSession().set(SESSION_KEY, objectMapper.writeValueAsString(currentUser));
        } catch (Exception e) {
            throw new IllegalStateException("登录会话写入失败", e);
        }
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setTokenName(tokenInfo.getTokenName());
        loginResponse.setTokenValue(tokenInfo.getTokenValue());
        loginResponse.setUserId(sysUser.getId());
        loginResponse.setNickname(sysUser.getNickname());
        loginResponse.setTenantId(tenant.getId());
        loginResponse.setDeptId(sysUser.getDeptId());
        return loginResponse;
    }

    @Override
    public CurrentUser getCurrentUser() {
        if (StpUtil.getLoginIdDefaultNull() == null) return null;
        Object json = StpUtil.getSession().get(SESSION_KEY);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json.toString(), CurrentUser.class);
        } catch (Exception e) {
            return null;
        }
    }
}
