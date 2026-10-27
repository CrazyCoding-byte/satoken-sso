package yzx.sacommon.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色表 sys_role
 * <p>
 * data_scope 绑定在角色上，用户多角色时取并集。
 */
@Data
@TableName("sys_role")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属租户 */
    private Long tenantId;

    /** 角色名称 */
    private String roleName;

    /** 角色标识，如 tenant_admin */
    private String roleKey;

    /** 数据范围：1全部 2本部门 3本部门及下级 4仅本人 5自定义 */
    private Integer dataScope;

    /** 显示顺序 */
    private Integer orderNum;

    /** 状态：0正常 1停用 */
    private String status;

    /** 删除标志：0存在 1删除 */
    @TableLogic(value = "0", delval = "1")
    private String delFlag;

    /** 创建时间 */
    private LocalDateTime createTime;
}
