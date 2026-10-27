package yzx.samodel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 业务订单表 biz_order
 * <p>
 * 三个隔离字段分别对应三种越权维度：
 * tenant_id 跨租户、dept_id 水平越权、create_by 仅本人。
 */
@Data
@TableName("biz_order")
public class BizOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户边界 */
    private Long tenantId;

    /** 数据归属部门，水平越权用 */
    private Long deptId;

    /** 订单号，租户内唯一 */
    private String orderNo;

    /** 金额 */
    private BigDecimal amount;

    /** 创建人，SELF 范围用 */
    private Long createBy;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 删除标志：0存在 1删除 */
    @TableLogic(value = "0", delval = "1")
    private String delFlag;
}
