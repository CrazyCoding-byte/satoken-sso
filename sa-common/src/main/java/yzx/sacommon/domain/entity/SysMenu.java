package yzx.sacommon.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单权限表 sys_menu
 * <p>
 * 全局共享，不含 tenant_id：菜单描述的是系统能力，不属于任何租户。
 * 后续必须加入租户拦截器的忽略列表。
 */
@Data
@TableName("sys_menu")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父菜单ID，0为根 */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 菜单类型：M目录 C菜单 F按钮 */
    private String menuType;

    /** 路由地址 */
    private String path;

    /** 权限码，如 user:add */
    private String perms;

    /** 显示顺序 */
    private Integer orderNum;

    /** 是否显示：0显示 1隐藏 */
    private String visible;

    /** 状态：0正常 1停用 */
    private String status;
}
