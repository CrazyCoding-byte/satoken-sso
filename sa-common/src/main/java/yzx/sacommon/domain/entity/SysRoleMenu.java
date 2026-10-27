package yzx.sacommon.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色菜单关联表 sys_role_menu
 * <p>
 * 联合主键 (role_id, menu_id)，没有自增主键。
 */
@Data
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long roleId;

    private Long menuId;

    /** 冗余字段，让租户拦截器统一过滤 */
    private Long tenantId;
}
