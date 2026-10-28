package yzx.saauth.service;

import yzx.saauth.dto.LoginRequest;
import yzx.saauth.dto.LoginResponse;
import yzx.sacommon.domain.dto.CurrentUser;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    /** 从当前会话还原用户，未登录返回 null */
    CurrentUser getCurrentUser();
}