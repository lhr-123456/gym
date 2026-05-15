package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachHomework;
import com.gym.service.CoachHomeworkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coach/homework")
public class CoachHomeworkController {

    private final CoachHomeworkService homeworkService;

    public CoachHomeworkController(CoachHomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    /**
     * 获取教练布置的作业列表
     */
    @GetMapping("/list")
    public ApiResponse<List<CoachHomework>> getList(
            @RequestParam Long coachId,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Integer status) {
        return ApiResponse.success(homeworkService.listByCoach(coachId));
    }

    /**
     * 分页获取教练布置的作业列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<CoachHomework>> getPage(
            @RequestParam Long coachId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Integer status) {
        Page<CoachHomework> page = homeworkService.pageByCoach(coachId, pageNum, pageSize, memberId, status);
        return ApiResponse.success(page);
    }

    /**
     * 获取作业详情
     */
    @GetMapping("/{id}")
    public ApiResponse<CoachHomework> getById(@PathVariable Long id) {
        return ApiResponse.success(homeworkService.getById(id));
    }

    /**
     * 布置新作业
     */
    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachHomework homework) {
        boolean ok = homeworkService.save(homework);
        if (ok) {
            return ApiResponse.success("布置作业成功");
        }
        return ApiResponse.error("布置作业失败");
    }

    /**
     * 更新作业
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachHomework homework) {
        boolean ok = homeworkService.updateById(homework);
        if (ok) {
            return ApiResponse.success("更新作业成功");
        }
        return ApiResponse.error("更新作业失败");
    }

    /**
     * 删除作业
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        homeworkService.deleteById(id);
        return ApiResponse.success("删除成功");
    }

    /**
     * 添加教练备注
     */
    @PutMapping("/remark/{id}")
    public ApiResponse<String> addRemark(@PathVariable Long id, @RequestParam String remark) {
        homeworkService.addCoachRemark(id, remark);
        return ApiResponse.success("备注已添加");
    }
}
