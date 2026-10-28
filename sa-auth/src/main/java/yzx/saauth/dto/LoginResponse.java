package yzx.saauth.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String tokenName;
    private String tokenValue;
    private Long userId;
    private String nickname;
    private Long tenantId;
    private Long deptId;
    /** 00 平台超管 01 租户用户 */
    private String userType;
}