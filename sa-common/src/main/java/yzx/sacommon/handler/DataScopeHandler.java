package yzx.sacommon.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import yzx.sacommon.domain.dto.CurrentUser;
import yzx.sacommon.domain.dto.DataScopeRule;
import yzx.sacommon.enums.DataScopeEnum;
import yzx.sacommon.security.CurrentUserHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @className: DataScopeHandler
 * @author: yzx
 * @date: 2026/10/27 12:01
 * @Version: 1.0
 * @description:
 */
public class DataScopeHandler implements MultiDataPermissionHandler {
    /** 系统表不套业务数据权限，否则查部门树本身会被部门权限过滤 */
    private static final Set<String> IGNORE_TABLES = Set.of(
            "sys_tenant", "sys_dept", "sys_role", "sys_role_dept",
            "sys_role_menu", "sys_menu", "sys_user_role");

    /**
     *
     * @param table             table 当前被处理的那张表
     * @param where             where原sql里已经有的where条件
     * @param mappedStatementId 当前Mapper方法的全名
     * @return null:不追加任何条件放行 1=0:恒假,一行都查不到 条件表达式:正常过滤
     */
    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        CurrentUser currentUser = CurrentUserHolder.get();
        if (currentUser == null) {
            return null;
        }
        // 平台超管:身份短路,不依赖角色配置
        if ("00".equals(currentUser.getUserType())) {
            return null;
        }
        if (IGNORE_TABLES.contains(table.getName().toLowerCase())) {
            return null;
        }
        List<DataScopeRule> rules = currentUser.getRules();
        if (rules == null || rules.isEmpty()) {
            // 没有任何角色,安全默认:1=0,一行都查不到
            return parse("1=0");
        }
        for (DataScopeRule rule : rules) {
            // 如果用户包含所有的权限就直接返回
            if (DataScopeEnum.ALL.getCode().equals(rule.getScope())) {
                return null;
            }
        }
        // 有别名必须用别名,是否join查询会报位置列
        String alias = table.getAlias() != null ? table.getAlias().getName() : table.getName();
        return parse(buildSegment(alias, currentUser, rules));
    }

    private String buildSegment(String alias, CurrentUser currentUser, List<DataScopeRule> rules) {
        List<String> parts = new ArrayList<>();
        for (DataScopeRule rule : rules) {
            DataScopeEnum scope = DataScopeEnum.of(rule.getScope());
            if (scope == null) {
                continue;
            }
            switch (scope) {
                case DEPT ->
                        parts.add(currentUser.getDeptId() == null ? "1=0" : alias + ".dept_id=" + currentUser.getDeptId());
                case DEPT_AND_CHILD -> parts.add(inClause(alias, rule.getDeptIds()));
                case CUSTOM -> parts.add(inClause(alias, rule.getDeptIds()));
                case SELF -> parts.add(alias + ".create_by=" + currentUser.getUserId());
                default -> {
                    break;
                }
            }
        }
        if(parts.isEmpty()) {
            return "1=0";
        }
        return "("+String.join(" OR ", parts)+")";
    }

    private String inClause(String alias, List<Long> deptIds) {
       if(deptIds==null || deptIds.isEmpty()) {
           return "1=0";
       }
       return alias+".dept_id in ("+deptIds.stream().map(String::valueOf).collect(Collectors.joining(","))+")";
    }

    private Expression parse(String segment) {
     try{
         return CCJSqlParserUtil.parseCondExpression(segment);
     }catch(JSQLParserException e){
         throw new IllegalArgumentException("数据权限条件解析失败：" + segment, e);
     }
    }
}
