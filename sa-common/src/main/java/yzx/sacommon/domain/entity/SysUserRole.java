package yzx.sacommon.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联表 sys_user_role
 * <p>
 * 联合主键 (user_id, role_id)，没有自增主键。
 * 这种表一般直接用 Wrapper 或自定义 SQL 操作，不要用 BaseMapper 的按 ID 方法。
 */
@Data
@TableName("sys_user_role")
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long roleId;

    /** 冗余字段，让租户拦截器统一过滤 */
    private Long tenantId;
}
