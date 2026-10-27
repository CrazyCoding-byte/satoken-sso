package yzx.sacommon.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表 sys_user
 * <p>
 * 唯一约束是 (tenant_id, username)，不同租户可以有同名账号。
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属租户 */
    private Long tenantId;

    /** 所属部门，数据权限 DEPT 的基础 */
    private Long deptId;

    /** 登录账号 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** BCrypt 哈希，自带盐 */
    private String password;

    /** 用户类型：00平台超管 01租户用户 */
    private String userType;

    /** 状态：0正常 1停用 */
    private String status;

    /** 删除标志：0存在 1删除 */
    @TableLogic(value = "0", delval = "1")
    private String delFlag;

    /** 创建人 */
    private Long createBy;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新人 */
    private Long updateBy;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
