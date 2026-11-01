package yzx.samodel.controller;

import cn.dev33.satoken.oauth2.annotation.SaCheckAccessToken;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yzx.sacommon.Utils.Result;
import yzx.samodel.domain.entity.BizOrder;
import yzx.samodel.service.BizOrderService;

/**
 * 对外开放接口：给第三方应用用的
 * <p>
 * 鉴权只认 access_token + scope，不认内部登录态；
 * 数据权限照样生效，因为 SaUserFilter 已经把 access_token 反查成了 CurrentUser。
 */
@RestController
@RequestMapping("/open-api/order")
@SaCheckAccessToken(scope = "order.read")
public class OpenOrderController {

    private final BizOrderService bizOrderService;

    public OpenOrderController(BizOrderService bizOrderService) {
        this.bizOrderService = bizOrderService;
    }

    @GetMapping("/page")
    public Result<IPage<BizOrder>> page(@RequestParam(defaultValue = "1") long current,
                                        @RequestParam(defaultValue = "10") long size) {
        return Result.success(bizOrderService.page(new Page<>(current, size)));
    }
}