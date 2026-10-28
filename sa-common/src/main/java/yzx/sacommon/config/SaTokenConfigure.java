package yzx.sacommon.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @className: SaTokenConfigure
 * @author: yzx
 * @date: 2026/10/28 14:00
 * @Version: 1.0
 * @description:
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",   // 登录接口本身不能要求登录
                        "/error"         // Spring Boot 错误转发，不排除会 401 死循环
                );
    }
}
