package yzx.sacommon.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 部门表 sys_dept
 * <p>
 * ancestors 存祖先路径（如 0,1,3），用于一条 SQL 查出"本部门及下级"。
 */
@Data
@TableName("sys_dept")
public class SysDept implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属租户 */
    private Long tenantId;

    /** 父部门ID，0为根 */
    private Long parentId;

    /** 祖先路径，如 0,1,3 */
    private String ancestors;

    /** 部门名称 */
    private String deptName;

    /** 显示顺序 */
    private Integer orderNum;

    /** 负责人 */
    private String leader;

    /** 状态：0正常 1停用 */
    private String status;

    /** 删除标志：0存在 1删除 */
    @TableLogic(value = "0", delval = "1")
    private String delFlag;

    /** 创建人 */
    private Long createBy;

    /** 创建时间 */
    private LocalDateTime createTime;
}
