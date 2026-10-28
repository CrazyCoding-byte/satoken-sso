package yzx.samodel.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yzx.sacommon.Utils.Result;
import yzx.samodel.domain.entity.BizOrder;
import yzx.samodel.service.BizOrderService;

@RestController
@RequestMapping("/order")
public class BizOrderController {

    private final BizOrderService bizOrderService;

    public BizOrderController(BizOrderService bizOrderService) {
        this.bizOrderService = bizOrderService;
    }

    /** 这个接口用来观察数据权限对 SQL 的改写效果 */
    @GetMapping("/page")
    @SaCheckPermission()
    public Result<IPage<BizOrder>> page(@RequestParam(defaultValue = "1") long current,
                                        @RequestParam(defaultValue = "10") long size) {
        return Result.success(bizOrderService.page(new Page<>(current, size)));
    }

    @GetMapping("/{id}")
    public Result<BizOrder> get(@PathVariable Long id) {
        return Result.success(bizOrderService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody BizOrder entity) {
        return Result.success(bizOrderService.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody BizOrder entity) {
        return Result.success(bizOrderService.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(bizOrderService.removeById(id));
    }
}
