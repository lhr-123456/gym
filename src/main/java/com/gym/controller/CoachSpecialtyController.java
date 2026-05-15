package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachSpecialty;
import com.gym.service.CoachSpecialtyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coach-specialty")
public class CoachSpecialtyController {

    private final CoachSpecialtyService coachSpecialtyService;

    public CoachSpecialtyController(CoachSpecialtyService coachSpecialtyService) {
        this.coachSpecialtyService = coachSpecialtyService;
    }

    /**
     * 分页查询教练专长
     */
    @GetMapping("/page")
    public ApiResponse<Page<CoachSpecialty>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CoachSpecialty coachSpecialty) {
        Page<CoachSpecialty> page = new Page<>(pageNum, pageSize);
        Page<CoachSpecialty> result = coachSpecialtyService.page(page);
        return ApiResponse.success(result);
    }

    /**
     * 查询教练专长列表
     */
    @GetMapping("/list")
    public ApiResponse<List<CoachSpecialty>> getList(CoachSpecialty coachSpecialty) {
        List<CoachSpecialty> list = coachSpecialtyService.list();
        return ApiResponse.success(list);
    }

    /**
     * 根据ID查询教练专长
     */
    @GetMapping("/{id}")
    public ApiResponse<CoachSpecialty> getById(@PathVariable Long id) {
        CoachSpecialty coachSpecialty = coachSpecialtyService.getById(id);
        if (coachSpecialty == null) {
            return ApiResponse.error("专长不存在");
        }
        return ApiResponse.success(coachSpecialty);
    }

    /**
     * 添加教练专长
     */
    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachSpecialty coachSpecialty) {
        boolean result = coachSpecialtyService.save(coachSpecialty);
        if (result) {
            return ApiResponse.success("添加专长成功");
        }
        return ApiResponse.error("添加专长失败");
    }

    /**
     * 更新教练专长
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachSpecialty coachSpecialty) {
        boolean result = coachSpecialtyService.updateById(coachSpecialty);
        if (result) {
            return ApiResponse.success("更新专长成功");
        }
        return ApiResponse.error("更新专长失败");
    }

    /**
     * 删除教练专长
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = coachSpecialtyService.removeById(id);
        if (result) {
            return ApiResponse.success("删除专长成功");
        }
        return ApiResponse.error("删除专长失败");
    }
}
