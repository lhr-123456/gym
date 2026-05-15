package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CourseBooking;
import com.gym.service.CourseBookingService;
import com.gym.service.MemberMessageService;
import com.gym.service.MemberPointsRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-booking")
public class CourseBookingController {

    private final CourseBookingService courseBookingService;
    private final MemberMessageService memberMessageService;
    private final MemberPointsRecordService pointsRecordService;

    public CourseBookingController(CourseBookingService courseBookingService,
                                  MemberMessageService memberMessageService,
                                  MemberPointsRecordService pointsRecordService) {
        this.courseBookingService = courseBookingService;
        this.memberMessageService = memberMessageService;
        this.pointsRecordService = pointsRecordService;
    }

    /**
     * 分页查询课程预约记录
     */
    @GetMapping("/page")
    public ApiResponse<Page<CourseBooking>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CourseBooking courseBooking) {
        Page<CourseBooking> page = courseBookingService.getPageWithDetails(pageNum, pageSize, courseBooking);
        return ApiResponse.success(page);
    }

    /**
     * 查询课程预约记录列表（不分页）
     */
    @GetMapping("/list")
    public ApiResponse<List<CourseBooking>> getList(CourseBooking courseBooking) {
        List<CourseBooking> list = courseBookingService.getListWithDetails(courseBooking);
        return ApiResponse.success(list);
    }

    /**
     * 根据ID查询课程预约详情
     */
    @GetMapping("/{id}")
    public ApiResponse<CourseBooking> getById(@PathVariable Long id) {
        CourseBooking courseBooking = courseBookingService.getById(id);
        if (courseBooking == null) {
            return ApiResponse.error("课程预约不存在");
        }
        return ApiResponse.success(courseBooking);
    }

    /**
     * 新增课程预约记录
     */
    @PostMapping
    public ApiResponse<String> save(@RequestBody CourseBooking courseBooking) {
        boolean result = courseBookingService.save(courseBooking);
        if (result) {
            return ApiResponse.success("添加课程预约成功");
        }
        return ApiResponse.error("添加课程预约失败");
    }

    /**
     * 更新课程预约记录
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody CourseBooking courseBooking) {
        boolean result = courseBookingService.updateById(courseBooking);
        if (result) {
            return ApiResponse.success("更新课程预约成功");
        }
        return ApiResponse.error("更新课程预约失败");
    }

    /**
     * 删除课程预约记录
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = courseBookingService.removeById(id);
        if (result) {
            return ApiResponse.success("删除课程预约成功");
        }
        return ApiResponse.error("删除课程预约失败");
    }

    /**
     * 审核通过课程预约
     */
    @PutMapping("/approve/{id}")
    public ApiResponse<String> approve(@PathVariable Long id) {
        try {
            CourseBooking booking = courseBookingService.getById(id);
            boolean result = courseBookingService.approveBooking(id);
            if (result) {
                // 推送消息给会员
                if (booking != null && booking.getMemberId() != null) {
                    memberMessageService.pushMessage(
                        booking.getMemberId(), "course",
                        "预约审核通过",
                        "您预约的课程已审核通过，请按时参加。",
                        String.valueOf(booking.getBookingId()), "course_booking"
                    );
                }
                return ApiResponse.success("审核通过");
            }
            return ApiResponse.error("审核失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 审核拒绝课程预约
     */
    @PutMapping("/reject/{id}")
    public ApiResponse<String> reject(@PathVariable Long id, @RequestParam String reason) {
        try {
            CourseBooking booking = courseBookingService.getById(id);
            boolean result = courseBookingService.rejectBooking(id, reason);
            if (result) {
                // 推送消息给会员
                if (booking != null && booking.getMemberId() != null) {
                    memberMessageService.pushMessage(
                        booking.getMemberId(), "course",
                        "预约审核拒绝",
                        "您预约的课程未通过审核，原因：" + reason,
                        String.valueOf(booking.getBookingId()), "course_booking"
                    );
                }
                return ApiResponse.success("拒绝成功");
            }
            return ApiResponse.error("拒绝失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 取消课程预约
     */
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
                // 推送消息给会员
                if (booking.getMemberId() != null) {
                    memberMessageService.pushMessage(
                        booking.getMemberId(), "course",
                        "课程取消通知",
                        "您预约的课程已取消，请知悉。",
                        String.valueOf(booking.getBookingId()), "course_booking"
                    );
                }
                return ApiResponse.success("取消成功");
            }
            return ApiResponse.error("取消失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 课程签到
     */
    @PutMapping("/signin/{id}")
    public ApiResponse<String> signIn(@PathVariable Long id) {
        try {
            boolean result = courseBookingService.signIn(id);
            if (result) {
                // 推送消息给会员
                CourseBooking booking = courseBookingService.getById(id);
                if (booking != null && booking.getMemberId() != null) {
                    memberMessageService.pushMessage(
                        booking.getMemberId(), "course",
                        "课程签到成功",
                        "您已成功签到，祝您训练愉快！",
                        String.valueOf(booking.getBookingId()), "course_booking"
                    );
                    // 记录完成课程积分
                    pointsRecordService.recordPoints(
                        booking.getMemberId(),
                        "complete_course",
                        String.valueOf(booking.getBookingId()),
                        "course_booking",
                        null
                    );
                }
                return ApiResponse.success("签到成功");
            }
            return ApiResponse.error("签到失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 开始课程（将预约状态改为已签到）
     */
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
                // 记录完成课程积分
                if (booking.getMemberId() != null) {
                    pointsRecordService.recordPoints(
                        booking.getMemberId(),
                        "complete_course",
                        String.valueOf(booking.getBookingId()),
                        "course_booking",
                        null
                    );
                }
                return ApiResponse.success("课程已开始");
            }
            return ApiResponse.error("开始课程失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
