package yzx.saauth.controller;

import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import yzx.sacommon.domain.entity.SysUser;
import yzx.sacommon.mapper.SysUserMapper;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class OAuth2UserInfoController {

    private final SysUserMapper userMapper;

    public OAuth2UserInfoController(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/oauth2/userinfo")
    public Map<String, Object> userinfo(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        String accessToken = extractBearer(authorization);
        if (accessToken == null) {
            throw new SaOAuth2Exception("缺少 access_token");
        }

        Object loginId = SaOAuth2Util.getLoginIdByAccessToken(accessToken);
        if (loginId == null) {
            throw new SaOAuth2Exception("access_token 无效或已过期");
        }

        // 必须校验 scope，否则拿到只有 order.read 的 token 也能查用户
        SaOAuth2Util.checkAccessTokenScope(accessToken, "user.read");

        SysUser user = userMapper.selectById((String) loginId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sub", loginId.toString());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        result.put("tenantId", user.getTenantId());
        return result;
    }

    private String extractBearer(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7).trim();
    }
}