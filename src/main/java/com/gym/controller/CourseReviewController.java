package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CourseReview;
import com.gym.service.CourseReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course/review")
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    public CourseReviewController(CourseReviewService courseReviewService) {
        this.courseReviewService = courseReviewService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<CourseReview>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CourseReview courseReview) {
        Page<CourseReview> page = courseReviewService.getPage(pageNum, pageSize, courseReview);
        return ApiResponse.success(page);
    }

    @GetMapping("/list")
    public ApiResponse<List<CourseReview>> getList(CourseReview courseReview) {
        List<CourseReview> list = courseReviewService.getList(courseReview);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseReview> getById(@PathVariable Long id) {
        CourseReview courseReview = courseReviewService.getById(id);
        if (courseReview == null) {
            return ApiResponse.error("评价不存在");
        }
        return ApiResponse.success(courseReview);
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<CourseReview>> getCourseReviews(@PathVariable Long courseId) {
        List<CourseReview> list = courseReviewService.getCourseReviews(courseId);
        return ApiResponse.success(list);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody CourseReview courseReview) {
        boolean result = courseReviewService.save(courseReview);
        if (result) {
            return ApiResponse.success("添加评价成功");
        }
        return ApiResponse.error("添加评价失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CourseReview courseReview) {
        if (courseReview.getReviewId() == null) {
            return ApiResponse.error("评价 ID 不能为空");
        }
        boolean result = courseReviewService.updateById(courseReview);
        if (result) {
            return ApiResponse.success("更新评价成功");
        }
        return ApiResponse.error("更新评价失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = courseReviewService.deleteById(id);
        if (result) {
            return ApiResponse.success("删除评价成功");
        }
        return ApiResponse.error("删除评价失败");
    }

    @PostMapping("/reply/{id}")
    public ApiResponse<String> reply(@PathVariable Long id, @RequestParam String reply) {
        try {
            boolean result = courseReviewService.reply(id, reply);
            if (result) {
                return ApiResponse.success("回复成功");
            }
            return ApiResponse.error("回复失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
