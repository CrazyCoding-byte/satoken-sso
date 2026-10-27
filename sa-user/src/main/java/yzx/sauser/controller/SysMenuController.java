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
import yzx.sacommon.domain.entity.SysMenu;
import yzx.sauser.service.SysMenuService;

@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    public SysMenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    @GetMapping("/page")
    public Result<IPage<SysMenu>> page(@RequestParam(defaultValue = "1") long current,
                                       @RequestParam(defaultValue = "10") long size) {
        return Result.success(sysMenuService.page(new Page<>(current, size)));
    }

    @GetMapping("/{id}")
    public Result<SysMenu> get(@PathVariable Long id) {
        return Result.success(sysMenuService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody SysMenu entity) {
        return Result.success(sysMenuService.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody SysMenu entity) {
        return Result.success(sysMenuService.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(sysMenuService.removeById(id));
    }
}
