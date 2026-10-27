package yzx.sauser.controller;

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
import yzx.sacommon.domain.entity.SysTenant;
import yzx.sauser.service.SysTenantService;

@RestController
@RequestMapping("/system/tenant")
public class SysTenantController {

    private final SysTenantService sysTenantService;

    public SysTenantController(SysTenantService sysTenantService) {
        this.sysTenantService = sysTenantService;
    }

    @GetMapping("/page")
    public Result<IPage<SysTenant>> page(@RequestParam(defaultValue = "1") long current,
                                         @RequestParam(defaultValue = "10") long size) {
        return Result.success(sysTenantService.page(new Page<>(current, size)));
    }

    @GetMapping("/{id}")
    public Result<SysTenant> get(@PathVariable Long id) {
        return Result.success(sysTenantService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody SysTenant entity) {
        return Result.success(sysTenantService.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody SysTenant entity) {
        return Result.success(sysTenantService.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(sysTenantService.removeById(id));
    }
}
