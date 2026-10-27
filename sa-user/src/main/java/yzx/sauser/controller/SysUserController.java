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
import yzx.sacommon.domain.entity.SysUser;
import yzx.sauser.service.SysUserService;

@RestController
@RequestMapping("/system/user")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @GetMapping("/page")
    public Result<IPage<SysUser>> page(@RequestParam(defaultValue = "1") long current,
                                       @RequestParam(defaultValue = "10") long size) {
        return Result.success(sysUserService.page(new Page<>(current, size)));
    }

    @GetMapping("/{id}")
    public Result<SysUser> get(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    /**
     * 新增用户。
     * 注意：password 目前是明文落库，接入登录后必须改成 BCrypt 加密。
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody SysUser entity) {
        return Result.success(sysUserService.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody SysUser entity) {
        return Result.success(sysUserService.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(sysUserService.removeById(id));
    }
}
