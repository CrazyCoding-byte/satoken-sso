package yzx.saauth.oauth2;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import yzx.saauth.dto.LoginRequest;
import yzx.saauth.service.AuthService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Sa-Token 的 OAuth2 组件是静态单例，必须启动时手动塞进去，不能靠 Spring 注入。
 */
@Component
public class OAuth2ServerConfiguration {

    private final DbOAuth2DataLoader dataLoader;
    private final AuthService authService;

    public OAuth2ServerConfiguration(DbOAuth2DataLoader dataLoader, AuthService authService) {
        this.dataLoader = dataLoader;
        this.authService = authService;
    }

    @PostConstruct
    public void init() {
        // 1. 应用信息来源
        SaOAuth2Manager.setDataLoader(dataLoader);

        // 2. 四种模式全开 + 令牌有效期
        SaOAuth2ServerConfig cfg = SaOAuth2Manager.getServerConfig();
        cfg.enableAuthorizationCode = true;
        cfg.enableImplicit = false;
        cfg.enablePassword = true;
        cfg.enableClientCredentials = true;
        cfg.codeTimeout = 300;              // code 5 分钟
        cfg.accessTokenTimeout = 7200;      // access_token 2 小时
        cfg.refreshTokenTimeout = 2592000;  // refresh_token 30 天
        cfg.clientTokenTimeout = 7200;
        cfg.isNewRefresh = true;            // 刷新时轮换 refresh_token

        // 3. 未登录页面：引导去登录，并把本次 authorize 的地址带下去
        SaOAuth2Strategy.instance.notLoginView = () -> {
            SaHolder.getResponse().setHeader("Content-Type", "text/html;charset=utf-8");
            String returnUrl = buildAuthorizeUrl(SaHolder.getRequest());
            return """
                    <html><body style="font-family:sans-serif;padding:40px">
                      <h3>请先登录</h3>
                      <form method="post" action="/oauth2/doLogin">
                        <input type="hidden" name="returnUrl" value="%s">
                        <p><input name="tenantCode" placeholder="租户编码" value="t1"></p>
                        <p><input name="name" placeholder="用户名"></p>
                        <p><input name="pwd" placeholder="密码"></p>
                        <p><button type="submit">登录并授权</button></p>
                      </form>
                    </body></html>
                    """.formatted(escapeHtml(returnUrl));
        };

        // 4. 授权确认页：提交后必须由前端跟随 redirect_uri
        SaOAuth2Strategy.instance.confirmView = (clientId, scopes) -> {
            SaHolder.getResponse().setHeader("Content-Type", "text/html;charset=utf-8");
            String scopeStr = String.join(",", scopes);
            return """
                    <html><body style="font-family:sans-serif;padding:40px">
                      <h3>授权确认</h3>
                      <p>应用 <b>%s</b> 申请以下权限：</p>
                      <p><code>%s</code></p>
                      <p><button onclick="doConfirm()">同意授权</button></p>
                    <script>
                      function doConfirm() {
                        const body = new URLSearchParams({ client_id: '%s', scope: '%s' });
                        fetch('/oauth2/doConfirm', {
                          method: 'POST',
                          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                          body: body.toString()
                        }).then(r => r.json()).then(d => {
                          // SaResult 继承 HashMap，redirect_uri 在最外层，不是 d.data
                          if (d.redirect_uri) {
                            location.href = d.redirect_uri;
                          } else {
                            alert(d.msg || '授权失败');
                          }
                        });
                      }
                    </script>
                    </body></html>
                    """.formatted(clientId, scopeStr, clientId, scopeStr);
        };

        // 5. 授权服务器自己的登录：复用业务登录，保证会话和权限快照一致
        SaOAuth2Strategy.instance.doLoginHandle = (name, pwd) -> {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setTenantCode(SaHolder.getRequest().getParam("tenantCode"));
            loginRequest.setUsername(name);
            loginRequest.setPassword(pwd);
            authService.login(loginRequest);

            // 关键：登录成功后必须 sendRedirect 回 authorize。
            // StpUtil.login 已经往响应里写了 satoken Cookie，浏览器下一次请求会带上，
            // authorize 里 getLoginIdDefaultNull() 才能拿到用户，继续走授权。
            String returnUrl = SaHolder.getRequest().getParam("returnUrl");
            SaHolder.getResponse().redirect(returnUrl == null || returnUrl.isBlank()
                    ? "/oauth2/authorize" : returnUrl);
            return null;   // 已经 sendRedirect，不要再写响应体
        };
    }

    /** 把当前 /oauth2/authorize?... 的参数重新拼成完整地址 */
    private String buildAuthorizeUrl(SaRequest req) {
        return req.getUrl()
                + "?response_type=" + enc(req.getParam("response_type", "code"))
                + "&client_id=" + enc(req.getParam("client_id"))
                + "&redirect_uri=" + enc(req.getParam("redirect_uri"))
                + "&scope=" + enc(req.getParam("scope"))
                + (req.getParam("state") == null ? "" : "&state=" + enc(req.getParam("state")));
    }

    /** 参数值里有中文/空格/逗号，必须先编码，否则拼进 URL 会乱 */
    private String enc(String value) {
        return value == null ? "" : URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /** 拼好的地址当 HTML 属性值时，& 要转义 */
    private String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
    }
}