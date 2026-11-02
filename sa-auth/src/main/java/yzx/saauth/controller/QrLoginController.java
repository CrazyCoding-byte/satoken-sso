package yzx.saauth.controller;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/qr")
public class QrLoginController {

    private static final String KEY_PREFIX = "qr:login:";
    private static final long TTL = 120;          // 二维码 2 分钟有效

    /** 1. PC 端：生成二维码（真实场景把这个 content 渲染成图片） */
    @GetMapping("/init")
    public Map<String, Object> init() {
        String qrToken = SaFoxUtil.getRandomString(32);
        String key = KEY_PREFIX + qrToken;

        Map<String, Object> qr = new LinkedHashMap<>();
        qr.put("status", "WAITING");
        SaManager.getSaTokenDao().setObject(key, qr, TTL);

        return Map.of(
                "qrToken", qrToken,
                "content", "http://localhost:8081/qr/mobile?qrToken=" + qrToken,
                "expireIn", TTL
        );
    }

    /** 2. 手机端：扫码（模拟时=用另一个浏览器打开 content 链接） */
    @GetMapping("/scan")
    public Map<String, Object> scan(@RequestParam String qrToken) {
        StpUtil.checkLogin();                       // 手机端必须已登录
        Map<String, Object> qr = getQr(qrToken);
        if (qr == null) {
            throw new RuntimeException("二维码已失效");
        }
        qr.put("status", "SCANNED");
        qr.put("loginId", StpUtil.getLoginIdAsString());
        saveQr(qrToken, qr);
        return Map.of("status", "SCANNED");
    }

    /** 3. 手机端：用户点「确认登录」 */
    @PostMapping("/confirm")
    public Map<String, Object> confirm(@RequestParam String qrToken) {
        StpUtil.checkLogin();
        Map<String, Object> qr = getQr(qrToken);
        if (qr == null) {
            throw new RuntimeException("二维码已失效");
        }
        if (!"SCANNED".equals(qr.get("status"))) {
            throw new RuntimeException("请先扫码");
        }
        // 防止 A 扫码、B 确认
        if (!StpUtil.getLoginIdAsString().equals(qr.get("loginId"))) {
            throw new RuntimeException("确认账号与扫码账号不一致");
        }
        qr.put("status", "CONFIRMED");
        saveQr(qrToken, qr);
        return Map.of("status", "CONFIRMED");
    }

    /** 4. PC 端：轮询状态，CONFIRMED 时在这里发 token */
    @GetMapping("/status")
    public Map<String, Object> status(@RequestParam String qrToken) {
        Map<String, Object> qr = getQr(qrToken);
        if (qr == null) {
            return Map.of("status", "EXPIRED");
        }
        String status = (String) qr.get("status");
        if ("CONFIRMED".equals(status)) {
            // 关键：只在这一刻，才替 PC 端建立登录态
            StpUtil.login(qr.get("loginId"));
            SaManager.getSaTokenDao().delete(KEY_PREFIX + qrToken);   // 一次性，用完即销毁
            return Map.of("status", "CONFIRMED", "token", StpUtil.getTokenValue());
        }
        return Map.of("status", status);
    }

    // ---------------- 模拟用的两个页面 ----------------

    /** 手机端页面（扫码后打开的确认页） */
    @GetMapping(value = "/mobile", produces = "text/html;charset=utf-8")
    public String mobile(@RequestParam String qrToken) {
        return """
                <html><body style="padding:24px;font-family:sans-serif">
                <h3>确认登录</h3>
                <p>票据：%s</p>
                <button onclick="go()">确认登录</button>
                <p id="msg" style="color:#666"></p>
                <script>
                  function go(){
                    fetch('/qr/scan?qrToken=%s', {credentials:'include'})
                      .then(r=>{ if(!r.ok) throw new Error('请先登录（未登录或已过期）'); return r.json(); })
                      .then(()=> fetch('/qr/confirm?qrToken=%s', {method:'POST', credentials:'include'}))
                      .then(r=>r.json())
                      .then(d=>document.getElementById('msg').textContent='已确认：'+d.status)
                      .catch(e=>document.getElementById('msg').textContent=e.message);
                  }
                </script>
                </body></html>
                """.formatted(qrToken, qrToken, qrToken);
    }

    /** PC 端页面：生成二维码 + 轮询 */
    @GetMapping(value = "/pc", produces = "text/html;charset=utf-8")
    public String pc() {
        return """
                <html><body style="padding:24px;font-family:sans-serif">
                <h3>扫码登录</h3>
                <p>二维码内容（真实场景渲染成图片）：</p>
                <p><a id="link" target="_blank"></a></p>
                <p>状态：<b id="st">-</b></p>
                <p id="tk" style="word-break:break-all;color:#0a7"></p>
                <script>
                  let qrToken;
                  fetch('/qr/init').then(r=>r.json()).then(d=>{
                    qrToken = d.qrToken;
                    const a = document.getElementById('link');
                    a.href = d.content; a.textContent = d.content;
                    poll();
                  });
                  function poll(){
                    fetch('/qr/status?qrToken='+qrToken, {credentials:'include'})
                      .then(r=>r.json()).then(d=>{
                        document.getElementById('st').textContent = d.status;
                        if(d.token){ document.getElementById('tk').textContent = 'token: '+d.token; return; }
                        if(d.status==='EXPIRED') return;
                        setTimeout(poll, 1500);
                      });
                  }
                </script>
                </body></html>
                """;
    }

    // ---------------- 私有方法 ----------------

    @SuppressWarnings("unchecked")
    private Map<String, Object> getQr(String qrToken) {
        return (Map<String, Object>) SaManager.getSaTokenDao().getObject(KEY_PREFIX + qrToken);
    }

    private void saveQr(String qrToken, Map<String, Object> qr) {
        SaManager.getSaTokenDao().setObject(KEY_PREFIX + qrToken, qr, TTL);
    }
}