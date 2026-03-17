package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachShiftChange;
import com.gym.service.CoachShiftChangeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coach-shift")
public class CoachShiftChangeController {

    private final CoachShiftChangeService coachShiftChangeService;

    public CoachShiftChangeController(CoachShiftChangeService coachShiftChangeService) {
        this.coachShiftChangeService = coachShiftChangeService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<CoachShiftChange>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CoachShiftChange coachShiftChange) {
        Page<CoachShiftChange> page = new Page<>(pageNum, pageSize);
        Page<CoachShiftChange> result = coachShiftChangeService.page(page);
        return ApiResponse.success(result);
    }

    @GetMapping("/list")
    public ApiResponse<List<CoachShiftChange>> getList(CoachShiftChange coachShiftChange) {
        List<CoachShiftChange> list = coachShiftChangeService.list();
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<CoachShiftChange> getById(@PathVariable Long id) {
        CoachShiftChange coachShiftChange = coachShiftChangeService.getById(id);
        if (coachShiftChange == null) {
            return ApiResponse.error("调班不存在");
        }
        return ApiResponse.success(coachShiftChange);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachShiftChange coachShiftChange) {
        boolean result = coachShiftChangeService.save(coachShiftChange);
        if (result) {
            return ApiResponse.success("添加调班成功");
        }
        return ApiResponse.error("添加调班失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachShiftChange coachShiftChange) {
        boolean result = coachShiftChangeService.updateById(coachShiftChange);
        if (result) {
            return ApiResponse.success("更新调班成功");
        }
        return ApiResponse.error("更新调班失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = coachShiftChangeService.removeById(id);
        if (result) {
            return ApiResponse.success("删除调班成功");
        }
        return ApiResponse.error("删除调班失败");
    }
}
