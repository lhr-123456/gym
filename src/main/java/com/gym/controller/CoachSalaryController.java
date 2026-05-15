package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachSalary;
import com.gym.service.CoachSalaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coach-salary")
public class CoachSalaryController {

    private final CoachSalaryService coachSalaryService;

    public CoachSalaryController(CoachSalaryService coachSalaryService) {
        this.coachSalaryService = coachSalaryService;
    }

    /**
     * 分页查询教练工资
     */
    @GetMapping("/page")
    public ApiResponse<Page<CoachSalary>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CoachSalary coachSalary) {
        Page<CoachSalary> page = new Page<>(pageNum, pageSize);
        Page<CoachSalary> result = coachSalaryService.page(page);
        return ApiResponse.success(result);
    }

    /**
     * 查询教练工资列表
     */
    @GetMapping("/list")
    public ApiResponse<List<CoachSalary>> getList(CoachSalary coachSalary) {
        List<CoachSalary> list = coachSalaryService.list();
        return ApiResponse.success(list);
    }

    /**
     * 根据ID查询教练工资
     */
    @GetMapping("/{id}")
    public ApiResponse<CoachSalary> getById(@PathVariable Long id) {
        CoachSalary coachSalary = coachSalaryService.getById(id);
        if (coachSalary == null) {
            return ApiResponse.error("工资不存在");
        }
        return ApiResponse.success(coachSalary);
    }

    /**
     * 添加教练工资
     */
    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachSalary coachSalary) {
        boolean result = coachSalaryService.save(coachSalary);
        if (result) {
            return ApiResponse.success("添加工资成功");
        }
        return ApiResponse.error("添加工资失败");
    }

    /**
     * 更新教练工资
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachSalary coachSalary) {
        boolean result = coachSalaryService.updateById(coachSalary);
        if (result) {
            return ApiResponse.success("更新工资成功");
        }
        return ApiResponse.error("更新工资失败");
    }

    /**
     * 删除教练工资
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = coachSalaryService.removeById(id);
        if (result) {
            return ApiResponse.success("删除工资成功");
        }
        return ApiResponse.error("删除工资失败");
    }
}
