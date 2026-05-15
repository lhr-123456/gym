package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CourseReview;
import com.gym.service.CourseReviewService;
import com.gym.service.MemberMessageService;
import com.gym.service.MemberPointsRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course/review")
public class CourseReviewController {

    private final CourseReviewService courseReviewService;
    private final MemberMessageService memberMessageService;
    private final MemberPointsRecordService pointsRecordService;

    public CourseReviewController(CourseReviewService courseReviewService,
                                 MemberMessageService memberMessageService,
                                 MemberPointsRecordService pointsRecordService) {
        this.courseReviewService = courseReviewService;
        this.memberMessageService = memberMessageService;
        this.pointsRecordService = pointsRecordService;
    }

    /**
     * 分页查询课程评价列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<CourseReview>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CourseReview courseReview) {
        Page<CourseReview> page = courseReviewService.getPage(pageNum, pageSize, courseReview);
        return ApiResponse.success(page);
    }

    /**
     * 查询课程评价列表（不分页）
     */
    @GetMapping("/list")
    public ApiResponse<List<CourseReview>> getList(CourseReview courseReview) {
        List<CourseReview> list = courseReviewService.getList(courseReview);
        return ApiResponse.success(list);
    }

    /**
     * 根据ID查询课程评价详情
     */
    @GetMapping("/{id}")
    public ApiResponse<CourseReview> getById(@PathVariable Long id) {
        CourseReview courseReview = courseReviewService.getById(id);
        if (courseReview == null) {
            return ApiResponse.error("评价不存在");
        }
        return ApiResponse.success(courseReview);
    }

    /**
     * 查询指定课程的所有评价
     */
    @GetMapping("/course/{courseId}")
    public ApiResponse<List<CourseReview>> getCourseReviews(@PathVariable Long courseId) {
        List<CourseReview> list = courseReviewService.getCourseReviews(courseId);
        return ApiResponse.success(list);
    }

    /**
     * 新增课程评价
     */
    @PostMapping
    public ApiResponse<String> save(@RequestBody CourseReview courseReview) {
        boolean result = courseReviewService.save(courseReview);
        if (result) {
            // 记录评价积分
            if (courseReview.getMemberId() != null) {
                pointsRecordService.recordPoints(
                    courseReview.getMemberId(),
                    "review",
                    courseReview.getReviewId() != null ? String.valueOf(courseReview.getReviewId()) : null,
                    "course_review",
                    null
                );
            }
            return ApiResponse.success("添加评价成功");
        }
        return ApiResponse.error("添加评价失败");
    }

    /**
     * 更新课程评价
     */
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

    /**
     * 删除课程评价
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = courseReviewService.deleteById(id);
        if (result) {
            return ApiResponse.success("删除评价成功");
        }
        return ApiResponse.error("删除评价失败");
    }

    /**
     * 教练回复课程评价
     */
    @PostMapping("/reply/{id}")
    public ApiResponse<String> reply(@PathVariable Long id, @RequestParam String reply) {
        try {
            CourseReview review = courseReviewService.getById(id);
            boolean result = courseReviewService.reply(id, reply);
            if (result) {
                // 推送消息给会员
                if (review != null && review.getMemberId() != null) {
                    memberMessageService.pushMessage(
                        review.getMemberId(), "coach",
                        "教练已回复您的评价",
                        "教练回复：" + reply,
                        String.valueOf(review.getReviewId()), "course_review"
                    );
                }
                return ApiResponse.success("回复成功");
            }
            return ApiResponse.error("回复失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
