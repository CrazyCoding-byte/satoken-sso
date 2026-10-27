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
import yzx.sacommon.domain.entity.SysRole;
import yzx.sauser.service.SysRoleService;

@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @GetMapping("/page")
    public Result<IPage<SysRole>> page(@RequestParam(defaultValue = "1") long current,
                                       @RequestParam(defaultValue = "10") long size) {
        return Result.success(sysRoleService.page(new Page<>(current, size)));
    }

    @GetMapping("/{id}")
    public Result<SysRole> get(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody SysRole entity) {
        return Result.success(sysRoleService.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody SysRole entity) {
        return Result.success(sysRoleService.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(sysRoleService.removeById(id));
    }
}
