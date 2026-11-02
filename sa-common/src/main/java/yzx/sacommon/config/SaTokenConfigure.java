package yzx.sacommon.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.oauth2.annotation.handler.SaCheckAccessTokenHandler;
import cn.dev33.satoken.oauth2.annotation.handler.SaCheckClientIdSecretHandler;
import cn.dev33.satoken.oauth2.annotation.handler.SaCheckClientTokenHandler;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import jakarta.annotation.PostConstruct;
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
    /**
     * sa-token-oauth2 的注解处理器本来由 SPI 插件自动注册，
     * 这里显式注册一次，避免插件加载时机不确定导致 @SaCheckAccessToken 被静默跳过。
     * registerAnnotationHandler 内部是 Map.put，重复注册无害。
     */
    @PostConstruct
    public void registerOAuth2AnnotationHandlers() {
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckAccessTokenHandler());
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckClientTokenHandler());
        SaAnnotationStrategy.instance.registerAnnotationHandler(new SaCheckClientIdSecretHandler());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",   // 登录接口本身不能要求登录
                        "/error",         // Spring Boot 错误转发，不排除会 401 死循环
                        "/oauth2/**"
                );
    }
}
