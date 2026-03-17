package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CourseBooking;
import com.gym.service.CourseBookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-booking")
public class CourseBookingController {

    private final CourseBookingService courseBookingService;

    public CourseBookingController(CourseBookingService courseBookingService) {
        this.courseBookingService = courseBookingService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<CourseBooking>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CourseBooking courseBooking) {
        Page<CourseBooking> page = courseBookingService.getPageWithDetails(pageNum, pageSize, courseBooking);
        return ApiResponse.success(page);
    }

    @GetMapping("/list")
    public ApiResponse<List<CourseBooking>> getList(CourseBooking courseBooking) {
        List<CourseBooking> list = courseBookingService.getListWithDetails(courseBooking);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseBooking> getById(@PathVariable Long id) {
        CourseBooking courseBooking = courseBookingService.getById(id);
        if (courseBooking == null) {
            return ApiResponse.error("课程预约不存在");
        }
        return ApiResponse.success(courseBooking);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody CourseBooking courseBooking) {
        boolean result = courseBookingService.save(courseBooking);
        if (result) {
            return ApiResponse.success("添加课程预约成功");
        }
        return ApiResponse.error("添加课程预约失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CourseBooking courseBooking) {
        boolean result = courseBookingService.updateById(courseBooking);
        if (result) {
            return ApiResponse.success("更新课程预约成功");
        }
        return ApiResponse.error("更新课程预约失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = courseBookingService.removeById(id);
        if (result) {
            return ApiResponse.success("删除课程预约成功");
        }
        return ApiResponse.error("删除课程预约失败");
    }

    @PutMapping("/approve/{id}")
    public ApiResponse<String> approve(@PathVariable Long id) {
        try {
            boolean result = courseBookingService.approveBooking(id);
            if (result) {
                return ApiResponse.success("审核通过");
            }
            return ApiResponse.error("审核失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/reject/{id}")
    public ApiResponse<String> reject(@PathVariable Long id, @RequestParam String reason) {
        try {
            boolean result = courseBookingService.rejectBooking(id, reason);
            if (result) {
                return ApiResponse.success("拒绝成功");
            }
            return ApiResponse.error("拒绝失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/cancel/{id}")
    public ApiResponse<String> cancel(@PathVariable Long id) {
        try {
            CourseBooking booking = courseBookingService.getById(id);
            if (booking == null) {
                return ApiResponse.error("预约不存在");
            }
            if ("已取消".equals(booking.getStatus()) || "已完成".equals(booking.getStatus())) {
                return ApiResponse.error("该状态不能取消");
            }
            booking.setStatus("已取消");
            boolean result = courseBookingService.updateById(booking);
            if (result) {
                return ApiResponse.success("取消成功");
            }
            return ApiResponse.error("取消失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/signin/{id}")
    public ApiResponse<String> signIn(@PathVariable Long id) {
        try {
            boolean result = courseBookingService.signIn(id);
            if (result) {
                return ApiResponse.success("签到成功");
            }
            return ApiResponse.error("签到失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/start/{id}")
    public ApiResponse<String> start(@PathVariable Long id) {
        try {
            CourseBooking booking = courseBookingService.getById(id);
            if (booking == null) {
                return ApiResponse.error("预约不存在");
            }
            if (!"已预约".equals(booking.getStatus())) {
                return ApiResponse.error("只有已预约状态的课程才能开始");
            }
            booking.setStatus("已签到");
            boolean result = courseBookingService.updateById(booking);
            if (result) {
                return ApiResponse.success("课程已开始");
            }
            return ApiResponse.error("开始课程失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
