package yzx.sacommon.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色自定义数据范围表 sys_role_dept
 * <p>
 * 只服务 data_scope = 5（自定义）这一种范围。
 */
@Data
@TableName("sys_role_dept")
public class SysRoleDept implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long roleId;

    private Long deptId;

    /** 冗余字段，让租户拦截器统一过滤 */
    private Long tenantId;
}
