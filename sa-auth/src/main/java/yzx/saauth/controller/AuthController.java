package yzx.saauth.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import yzx.saauth.dto.LoginRequest;
import yzx.saauth.dto.LoginResponse;
import yzx.saauth.service.AuthService;
import yzx.sacommon.Utils.Result;
import yzx.sacommon.domain.dto.CurrentUser;

/**
 * @className: AuthController
 * @author: yzx
 * @date: 2026/10/28 10:37
 * @Version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Boolean> logout() {
        StpUtil.logout();
        return Result.success(true);
    }

    @GetMapping("/info")
    public Result<CurrentUser> info() {
        return Result.success(authService.getCurrentUser());
    }
}
