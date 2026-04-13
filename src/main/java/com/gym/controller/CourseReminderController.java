package com.gym.controller;

import com.gym.dto.ApiResponse;
import com.gym.entity.CourseReminder;
import com.gym.service.CourseReminderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member/reminder")
public class CourseReminderController {

    private final CourseReminderService reminderService;

    public CourseReminderController(CourseReminderService reminderService) {
        this.reminderService = reminderService;
    }

    /** 获取会员课程提醒列表 */
    @GetMapping("/list")
    public ApiResponse<List<CourseReminder>> getList(@RequestParam Long memberId) {
        return ApiResponse.success(reminderService.getByMemberId(memberId));
    }

    /** 获取会员即将到来的课程提醒 */
    @GetMapping("/upcoming")
    public ApiResponse<List<CourseReminder>> getUpcoming(@RequestParam Long memberId) {
        return ApiResponse.success(reminderService.getUpcomingByMemberId(memberId));
    }

    /** 创建课程提醒（由预约接口自动调用） */
    @PostMapping("/create")
    public ApiResponse<String> create(@RequestParam Long memberId,
                                     @RequestParam Long bookingId,
                                     @RequestParam Long courseId,
                                     @RequestParam String courseName,
                                     @RequestParam(required = false) String coachName,
                                     @RequestParam String classTime,
                                     @RequestParam(required = false) String location) {
        try {
            reminderService.createReminder(
                memberId, bookingId, courseId, courseName, coachName,
                java.time.LocalDateTime.parse(classTime), location
            );
            return ApiResponse.success("提醒已创建");
        } catch (Exception e) {
            return ApiResponse.error("创建提醒失败：" + e.getMessage());
        }
    }

    /** 标记提醒已发送 */
    @PutMapping("/mark/{id}")
    public ApiResponse<String> markSent(@PathVariable Long id) {
        reminderService.markReminderSent(id);
        return ApiResponse.success("已标记");
    }
}
