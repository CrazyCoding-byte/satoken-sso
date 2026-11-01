package yzx.sacommon.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yzx.sacommon.handler.DataScopeHandler;
import yzx.sacommon.tenant.SaTenantLineHandler;

/**
 * @className: MybatisPlusConfig
 * @author: yzx
 * @date: 2026/10/27 16:39
 * @Version: 1.0
 * @description:
 */
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 数据权限：必须排在分页前面
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new SaTenantLineHandler()));
        interceptor.addInnerInterceptor(new DataPermissionInterceptor(new DataScopeHandler()));

        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }
}
