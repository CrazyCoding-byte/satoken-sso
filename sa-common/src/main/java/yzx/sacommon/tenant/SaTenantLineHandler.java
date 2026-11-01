package yzx.sacommon.tenant;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import yzx.sacommon.domain.dto.CurrentUser;
import yzx.sacommon.security.CurrentUserHolder;

import java.util.Set;

/**
 * @className: SaTenantLineHandler
 * @author: yzx
 * @date: 2026/11/1 13:52
 * @Version: 1.0
 * @description:
 */
public class SaTenantLineHandler implements TenantLineHandler {

    private static final Set<String> IGNORE_TABLES = Set.of("sys_tenant", "sys_menu");

    @Override
    public Expression getTenantId() {
        CurrentUser currentUser = CurrentUserHolder.get();
        return new LongValue(currentUser.getTenantId());
    }

    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        CurrentUser currentUser = CurrentUserHolder.get();
        if (currentUser == null) return true;
        if ("00".equals(currentUser.getUserType())) {
            return true;
        }
        return IGNORE_TABLES.contains(tableName.toLowerCase());
    }
}
