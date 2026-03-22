package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CourseBooking;
import com.gym.entity.CourseInfo;
import com.gym.service.CourseInfoService;
import com.gym.service.MemberPointsRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseInfoController {

    private final CourseInfoService courseInfoService;
    private final MemberPointsRecordService pointsRecordService;

    public CourseInfoController(CourseInfoService courseInfoService,
                               MemberPointsRecordService pointsRecordService) {
        this.courseInfoService = courseInfoService;
        this.pointsRecordService = pointsRecordService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<CourseInfo>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CourseInfo courseInfo) {
        Page<CourseInfo> page = courseInfoService.getCoursePage(pageNum, pageSize, courseInfo);
        return ApiResponse.success(page);
    }

    @GetMapping("/available")
    public ApiResponse<Page<CourseInfo>> getAvailable(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CourseInfo courseInfo) {
        Page<CourseInfo> page = courseInfoService.getAvailableCourses(pageNum, pageSize, courseInfo);
        return ApiResponse.success(page);
    }

    @GetMapping("/bookings/{courseId}")
    public ApiResponse<List<CourseBooking>> getCourseBookings(@PathVariable Long courseId) {
        List<CourseBooking> bookings = courseInfoService.getCourseBookings(courseId);
        return ApiResponse.success(bookings);
    }

    @GetMapping("/list")
    public ApiResponse<List<CourseInfo>> getList(CourseInfo courseInfo) {
        List<CourseInfo> list = courseInfoService.list(courseInfo);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseInfo> getById(@PathVariable Long id) {
        CourseInfo courseInfo = courseInfoService.getById(id);
        if (courseInfo == null) {
            return ApiResponse.error("课程不存在");
        }
        return ApiResponse.success(courseInfo);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody CourseInfo courseInfo) {
        boolean result = courseInfoService.save(courseInfo);
        if (result) {
            return ApiResponse.success("添加课程成功");
        }
        return ApiResponse.error("添加课程失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CourseInfo courseInfo) {
        if (courseInfo.getCourseId() == null) {
            return ApiResponse.error("课程 ID 不能为空");
        }
        try {
            boolean result = courseInfoService.updateById(courseInfo);
            if (result) {
                return ApiResponse.success("更新课程成功");
            }
            return ApiResponse.error("更新课程失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("更新课程失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = courseInfoService.deleteById(id);
        if (result) {
            return ApiResponse.success("删除课程成功");
        }
        return ApiResponse.error("删除课程失败");
    }

    @PostMapping("/book/{courseId}")
    public ApiResponse<String> bookCourse(
            @PathVariable Long courseId,
            @RequestParam Long memberId,
            @RequestParam(required = false) Long coachId) {
        try {
            boolean result = courseInfoService.bookCourse(courseId, memberId, coachId);
            if (result) {
                // 记录积分明细
                pointsRecordService.recordPoints(
                    memberId,
                    "booking",
                    String.valueOf(courseId),
                    "course_booking",
                    null
                );
                return ApiResponse.success("预约课程成功");
            }
            return ApiResponse.error("预约课程失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/cancel/{bookingId}")
    public ApiResponse<String> cancelBooking(@PathVariable Long bookingId) {
        try {
            boolean result = courseInfoService.cancelBooking(bookingId);
            if (result) {
                return ApiResponse.success("取消预约成功");
            }
            return ApiResponse.error("取消预约失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
