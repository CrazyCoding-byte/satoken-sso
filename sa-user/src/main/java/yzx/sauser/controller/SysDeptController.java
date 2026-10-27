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
import yzx.sacommon.domain.entity.SysDept;
import yzx.sauser.service.SysDeptService;

@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    private final SysDeptService sysDeptService;

    public SysDeptController(SysDeptService sysDeptService) {
        this.sysDeptService = sysDeptService;
    }

    @GetMapping("/page")
    public Result<IPage<SysDept>> page(@RequestParam(defaultValue = "1") long current,
                                       @RequestParam(defaultValue = "10") long size) {
        return Result.success(sysDeptService.page(new Page<>(current, size)));
    }

    @GetMapping("/{id}")
    public Result<SysDept> get(@PathVariable Long id) {
        return Result.success(sysDeptService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody SysDept entity) {
        return Result.success(sysDeptService.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody SysDept entity) {
        return Result.success(sysDeptService.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(sysDeptService.removeById(id));
    }
}
