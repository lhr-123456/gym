package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachPerformance;
import com.gym.service.CoachPerformanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coach-performance")
public class CoachPerformanceController {

    private final CoachPerformanceService coachPerformanceService;

    public CoachPerformanceController(CoachPerformanceService coachPerformanceService) {
        this.coachPerformanceService = coachPerformanceService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<CoachPerformance>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CoachPerformance coachPerformance) {
        Page<CoachPerformance> page = new Page<>(pageNum, pageSize);
        Page<CoachPerformance> result = coachPerformanceService.getPage(page, coachPerformance);
        return ApiResponse.success(result);
    }

    @GetMapping("/list")
    public ApiResponse<List<CoachPerformance>> getList(CoachPerformance coachPerformance) {
        List<CoachPerformance> list = coachPerformanceService.list();
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<CoachPerformance> getById(@PathVariable Long id) {
        CoachPerformance coachPerformance = coachPerformanceService.getById(id);
        if (coachPerformance == null) {
            return ApiResponse.error("Performance not found");
        }
        return ApiResponse.success(coachPerformance);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachPerformance coachPerformance) {
        boolean result = coachPerformanceService.save(coachPerformance);
        if (result) {
            return ApiResponse.success("Add performance successfully");
        }
        return ApiResponse.error("Add performance failed");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachPerformance coachPerformance) {
        boolean result = coachPerformanceService.updateById(coachPerformance);
        if (result) {
            return ApiResponse.success("Update performance successfully");
        }
        return ApiResponse.error("Update performance failed");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = coachPerformanceService.removeById(id);
        if (result) {
            return ApiResponse.success("Delete performance successfully");
        }
        return ApiResponse.error("Delete performance failed");
    }
}
