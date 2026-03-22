package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachInfo;
import com.gym.service.CoachInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coach")
public class CoachInfoController {

    private final CoachInfoService coachInfoService;

    public CoachInfoController(CoachInfoService coachInfoService) {
        this.coachInfoService = coachInfoService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<CoachInfo>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CoachInfo coachInfo) {
        Page<CoachInfo> page = coachInfoService.getCoachPage(pageNum, pageSize, coachInfo);
        return ApiResponse.success(page);
    }

    @GetMapping("/list")
    public ApiResponse<List<CoachInfo>> getList(CoachInfo coachInfo) {
        List<CoachInfo> list = coachInfoService.list(coachInfo);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id:\\d+}")
    public ApiResponse<CoachInfo> getById(@PathVariable Long id) {
        CoachInfo coachInfo = coachInfoService.getById(id);
        if (coachInfo == null) {
            return ApiResponse.error("教练不存在");
        }
        return ApiResponse.success(coachInfo);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachInfo coachInfo) {
        boolean result = coachInfoService.save(coachInfo);
        if (result) {
            return ApiResponse.success("添加教练成功");
        }
        return ApiResponse.error("添加教练失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachInfo coachInfo) {
        if (coachInfo.getCoachId() == null) {
            return ApiResponse.error("教练 ID 不能为空");
        }
        boolean result = coachInfoService.updateById(coachInfo);
        if (result) {
            return ApiResponse.success("更新教练成功");
        }
        return ApiResponse.error("更新教练失败");
    }

    @DeleteMapping("/{id:\\d+}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = coachInfoService.deleteById(id);
        if (result) {
            return ApiResponse.success("删除教练成功");
        }
        return ApiResponse.error("删除教练失败");
    }
}
