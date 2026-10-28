package yzx.saauth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    /** 租户编码，登录页可让用户填，也可以从子域名推导 */
    private String tenantCode;
    private String username;
    private String password;
}