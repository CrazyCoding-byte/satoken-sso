package yzx.sacommon.filter;

import jakarta.servlet.ServletException;
import org.springframework.web.filter.OncePerRequestFilter;
import yzx.sacommon.domain.dto.CurrentUser;
import yzx.sacommon.domain.dto.DataScopeRule;
import yzx.sacommon.enums.DataScopeEnum;
import yzx.sacommon.security.CurrentUserHolder;
import yzx.sacommon.security.DataScopeResolver;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.List;

/**
 * @className: DebugUserFilter
 * @author: yzx
 * @date: 2026/10/27 15:58
 * @Version: 1.0
 * @description:
 */
public class DebugUserFilter extends OncePerRequestFilter {
    private final DataScopeResolver dataScopeResolver;

    public DebugUserFilter(DataScopeResolver dataScopeResolver) {
        this.dataScopeResolver = dataScopeResolver;
    }

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws ServletException, IOException {
        try {
            Long userId = parseUserId(request.getHeader("X-User-Id"));
            CurrentUser currentUser = userId == null ? mockUser() : dataScopeResolver.resolve(userId);
            filterChain.doFilter(request, response);
        } finally {
            CurrentUserHolder.clear();
        }
    }

    private Long parseUserId(String header) {
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(header.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private CurrentUser mockUser() {
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(1L);
        currentUser.setDeptId(1L);
        currentUser.setTenantId(1L);
        currentUser.setUserType("01");
        currentUser.getRules().add(new DataScopeRule(DataScopeEnum.DEPT.getCode(), List.of(1L)));
        return currentUser;
    }
}
