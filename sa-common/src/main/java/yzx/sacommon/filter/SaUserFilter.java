package yzx.sacommon.filter;

import cn.dev33.satoken.SaManager;
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
    private final ObjectMapper objectMapper;

    public SaUserFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String tokenName = SaManager.getConfig().getTokenName();
            String tokenValue = request.getHeader(tokenName);
            if (tokenValue != null && !tokenValue.isEmpty() && !tokenValue.isBlank()) {
                Object loginIdByToken = StpUtil.getLoginIdByToken(tokenValue);

            }
        } finally {
            CurrentUserHolder.clear();
        }
    }
}
