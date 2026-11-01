package yzx.sacommon.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_oauth_client")
public class SysOAuthClient implements Serializable {

    /** client_id 是业务主键，不是自增 */
    @TableId(type = IdType.INPUT)
    private String clientId;

    private String clientSecret;

    private String clientName;

    /** 允许的回调地址，逗号分隔 */
    private String redirectUri;

    /** 允许申请的 scope，逗号分隔 */
    private String contractScope;

    private Long tenantId;

    /** 0正常 1停用 */
    private String status;

    private LocalDateTime createTime;
}