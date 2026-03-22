package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachReview;
import com.gym.service.CoachReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coach-review")
public class CoachReviewController {

    private final CoachReviewService coachReviewService;

    public CoachReviewController(CoachReviewService coachReviewService) {
        this.coachReviewService = coachReviewService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<CoachReview>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CoachReview coachReview) {
        Page<CoachReview> page = new Page<>(pageNum, pageSize);
        Page<CoachReview> result = coachReviewService.page(page);
        return ApiResponse.success(result);
    }

    @GetMapping("/list")
    public ApiResponse<List<CoachReview>> getList(CoachReview coachReview) {
        List<CoachReview> list = coachReviewService.lambdaQuery()
                .eq(coachReview.getCoachId() != null, CoachReview::getCoachId, coachReview.getCoachId())
                .list();
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<CoachReview> getById(@PathVariable Long id) {
        CoachReview coachReview = coachReviewService.getById(id);
        if (coachReview == null) {
            return ApiResponse.error("评价不存在");
        }
        return ApiResponse.success(coachReview);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachReview coachReview) {
        boolean result = coachReviewService.save(coachReview);
        if (result) {
            return ApiResponse.success("添加评价成功");
        }
        return ApiResponse.error("添加评价失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachReview coachReview) {
        boolean result = coachReviewService.updateById(coachReview);
        if (result) {
            return ApiResponse.success("更新评价成功");
        }
        return ApiResponse.error("更新评价失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = coachReviewService.removeById(id);
        if (result) {
            return ApiResponse.success("删除评价成功");
        }
        return ApiResponse.error("删除评价失败");
    }
}
