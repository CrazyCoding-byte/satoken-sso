package yzx.saauth.controller;

import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 这 7 个端点全部交给 Sa-Token 的处理器分发，不要自己写业务逻辑。
 * 精确列出路径，不能写 /oauth2/**，否则会和 /oauth2/userinfo 冲突。
 */
@RestController
public class OAuth2ServerController {

    @RequestMapping({
            "/oauth2/authorize",
            "/oauth2/token",
            "/oauth2/refresh",
            "/oauth2/revoke",
            "/oauth2/client_token",
            "/oauth2/doLogin",
            "/oauth2/doConfirm"
    })
    public Object dister() {
        return SaOAuth2ServerProcessor.instance.dister();
    }
}