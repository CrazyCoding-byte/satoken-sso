package yzx.sacommon.config;

import java.io.Serializable;

/**
 * @className: LoginUser
 * @author: yzx
 * @date: 2026/10/27 11:11
 * @Version: 1.0
 * @description:
 */
public class LoginUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private String username;
    private Long tenantId;      // 后续所有 SQL 隔离的依据
    private String tenantCode;
    private Long deptId;        // 数据权限 DEPT 的依据
    private String userType;      // "00" 平台超管，认证层短路用
}
