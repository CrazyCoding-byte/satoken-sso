package yzx.sacommon.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yzx.sacommon.Utils.Result;

/**
 * @className: GlobalExceptionHandler
 * @author: yzx
 * @date: 2026/10/27 11:12
 * @Version: 1.0
 * @description:
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Result<Void>> handleNotLogin(NotLoginException e) {
        // 1. 判断 e.getType() 是否 KICK_OUT / BE_REPLACED
        // 2. 返回 401 + 对应文案
        if ("KICK_OUT".equals(e.getType()) || "BE_REPLACED".equals(e.getType())) {
            return ResponseEntity.status(401).body(Result.error(null));
        }
        return ResponseEntity.status(401).body(Result.error(null));
    }


}
