package yzx.sacommon.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 租户表 sys_tenant
 * <p>
 * 注意：租户表本身不参与租户隔离，后续必须加入租户拦截器的忽略列表。
 */
@Data
@TableName("sys_tenant")
public class SysTenant implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 租户ID，业务表统一引用 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户编码，登录页/子域名识别用 */
    private String tenantCode;

    /** 租户名称 */
    private String tenantName;

    /** 到期时间，SaaS 试用/续费用 */
    private LocalDateTime expireTime;

    /** 状态：0正常 1停用 */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;
}
