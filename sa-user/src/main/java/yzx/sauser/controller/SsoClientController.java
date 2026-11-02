package yzx.sauser.controller;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * 子系统侧的 SSO 客户端（OAuth2 授权码模式）
 * <p>
 * 本系统自己持有会话；只在 auth-server 判断"用户是否已登录"。
 */
@RestController
@RequestMapping("/sso")
public class SsoClientController {

    @Value("${sso.auth-server}")
    private String authServer;
    @Value("${sso.client-id}")
    private String clientId;
    @Value("${sso.client-secret}")
    private String clientSecret;
    @Value("${sso.redirect-uri}")
    private String redirectUri;

    private final RestClient restClient = RestClient.create();

    /**
     * 第一步：用户点「登录」→ 把浏览器送去授权服务器
     */
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String url = UriComponentsBuilder.fromUriString(authServer + "/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "openid,user.read")
                .build()
                .toUriString();
        response.sendRedirect(url);
    }

    /**
     * 第二步：授权服务器回调过来，带着 code
     * code 换 access_token（服务端之间调，client_secret 不进浏览器）→ 查用户 → 建本系统会话
     */
    @GetMapping("/callback")
    public void callback(@RequestParam String code, HttpServletResponse response) throws IOException {
        // 1. code 换 token
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("code", code);
        form.add("redirect_uri", redirectUri);

        Map<String, Object> tokenResp = restClient.post()
                .uri(authServer + "/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        Object accessToken = tokenResp == null ? null : tokenResp.get("access_token");
        if (accessToken == null) {
            response.sendRedirect("/sso/login");
            return;
        }

        // 2. 用 access_token 查用户身份（sub 就是 auth-server 里的 loginId）
        Map<String, Object> userInfo = restClient.get()
                .uri(authServer + "/oauth2/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        // 3. 在本系统建立会话（token 换成 user-token，与 auth-server 的 satoken 隔离）
        StpUtil.login(String.valueOf(userInfo.get("sub")));

        response.sendRedirect("/sso/me");
    }

    /** 验证用：当前本系统的登录用户 */
    @GetMapping("/me")
    public Map<String, Object> me() {
        return Map.of(
                "loginId", StpUtil.getLoginId(),
                "token", StpUtil.getTokenValue()
        );
    }

    /** 单点退出：先退本系统，再让浏览器去 auth-server 统一注销 */
    @GetMapping("/logout")
    public void logout(HttpServletResponse response) throws IOException {
        StpUtil.logout();
        response.sendRedirect(authServer + "/auth/logout");
    }
}