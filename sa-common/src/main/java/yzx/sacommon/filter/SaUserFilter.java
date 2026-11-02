package yzx.sacommon.filter;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;
import yzx.sacommon.domain.dto.CurrentUser;
import yzx.sacommon.security.CurrentUserHolder;

import java.io.IOException;

/**
 * @className: SaUserFilter
 * @author: yzx
 * @date: 2026/10/28 10:38
 * @Version: 1.0
 * @description:
 */
@Component
public class SaUserFilter extends OncePerRequestFilter {
    private static final String SESSION_KEY = "CURRENT_USER";
    private static final String BEARER_PREFIX = "Bearer ";
    private final ObjectMapper objectMapper;

    public SaUserFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Object loginId = resolveLoginId(request);
            if (loginId != null) {
                // false：会话不存在时返回 null，不要凭空创建一个空会话
                SaSession saSession = StpUtil.getSessionByLoginId(loginId, false);
                if (saSession != null) {
                    Object json = saSession.get(SESSION_KEY);
                    if (json != null) {
                        CurrentUser currentUser = objectMapper.readValue(json.toString(), CurrentUser.class);
                        CurrentUserHolder.set(currentUser);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            CurrentUserHolder.clear();
        }
    }

    private Object resolveLoginId(HttpServletRequest request) {
        String tokenName = SaManager.getConfig().getTokenName();
        String tokenValue = request.getHeader(tokenName);
        if (tokenValue != null && !tokenValue.isBlank()) {
            Object loginId = StpUtil.getLoginIdByToken(tokenValue);
            if (loginId != null) {
                return loginId;
            }
        }
        String accessToken = extractBearerToken(request.getHeader("Authorization"));
        if (accessToken != null) {
            AccessTokenModel at = SaOAuth2Util.getAccessToken(accessToken);
            if (at != null) {
                return at.loginId;
            }
        }
        return null;
    }

    private String extractBearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        if (!authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return null;
        }
        String value = authorization.substring(BEARER_PREFIX.length()).trim();
        return value.isEmpty() ? null : value;
    }
}
