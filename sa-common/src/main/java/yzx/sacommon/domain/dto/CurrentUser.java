package yzx.sacommon.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 当前登录用户（数据权限用）
 * <p>
 * 现在先由 DebugUserFilter 构造，接入 Sa-Token 后改为从 Session 读取。
 */
@Data
public class CurrentUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    /** 所属部门，本部门/本部门及下级范围用 */
    private Long deptId;

    private Long tenantId;

    /** 00 平台超管，01 租户用户 */
    private String userType;

    /** 多角色对应的多条规则，SQL 改写时用 OR 合并 */
    private List<DataScopeRule> rules = new ArrayList<>();
}
