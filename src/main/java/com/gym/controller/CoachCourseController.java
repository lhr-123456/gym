package com.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.*;
import com.gym.mapper.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/coach")
public class CoachCourseController {

    private final CoachScheduleMapper coachScheduleMapper;
    private final CourseBookingMapper courseBookingMapper;
    private final MemberInfoMapper memberInfoMapper;
    private final UserInfoMapper userInfoMapper;

    public CoachCourseController(CoachScheduleMapper coachScheduleMapper,
                                CourseBookingMapper courseBookingMapper,
                                MemberInfoMapper memberInfoMapper,
                                UserInfoMapper userInfoMapper) {
        this.coachScheduleMapper = coachScheduleMapper;
        this.courseBookingMapper = courseBookingMapper;
        this.memberInfoMapper = memberInfoMapper;
        this.userInfoMapper = userInfoMapper;
    }

    /**
     * 获取教练排课列表
     */
    @GetMapping("/schedule/page")
    public ApiResponse<Page<CoachSchedule>> getSchedulePage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Integer userType = (Integer) request.getAttribute("userType");

            if (userType == null || userType != 2) {
                return ApiResponse.error("无权限访问");
            }

            UserInfo userInfo = userInfoMapper.selectById(userId);
            if (userInfo == null || userInfo.getCoachId() == null) {
                return ApiResponse.error("教练信息不存在");
            }

            Long coachId = userInfo.getCoachId();

            Page<CoachSchedule> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CoachSchedule::getCoachId, coachId)
                   .eq(CoachSchedule::getDeleted, 0);

            if (startDate != null) {
                wrapper.ge(CoachSchedule::getScheduleDate, startDate);
            }
            if (endDate != null) {
                wrapper.le(CoachSchedule::getScheduleDate, endDate);
            }

            wrapper.orderByAsc(CoachSchedule::getScheduleDate)
                   .orderByAsc(CoachSchedule::getStartTime);

            Page<CoachSchedule> result = coachScheduleMapper.selectPage(page, wrapper);
            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取排课失败：" + e.getMessage());
        }
    }

    /**
     * 获取教练排课列表（无分页）
     */
    @GetMapping("/schedule/list")
    public ApiResponse<List<CoachSchedule>> getScheduleList(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Integer userType = (Integer) request.getAttribute("userType");

            if (userType == null || userType != 2) {
                return ApiResponse.error("无权限访问");
            }

            UserInfo userInfo = userInfoMapper.selectById(userId);
            if (userInfo == null || userInfo.getCoachId() == null) {
                return ApiResponse.error("教练信息不存在");
            }

            Long coachId = userInfo.getCoachId();

            LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CoachSchedule::getCoachId, coachId)
                   .eq(CoachSchedule::getDeleted, 0);

            if (date != null) {
                wrapper.eq(CoachSchedule::getScheduleDate, date);
            }
            if (startDate != null) {
                wrapper.ge(CoachSchedule::getScheduleDate, startDate);
            }
            if (endDate != null) {
                wrapper.le(CoachSchedule::getScheduleDate, endDate);
            }

            wrapper.orderByAsc(CoachSchedule::getScheduleDate)
                   .orderByAsc(CoachSchedule::getStartTime);

            List<CoachSchedule> list = coachScheduleMapper.selectList(wrapper);
            return ApiResponse.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取排课失败：" + e.getMessage());
        }
    }

    /**
     * 获取教练课程预约详情
     */
    @GetMapping("/schedule/bookings/{scheduleId}")
    public ApiResponse<List<Map<String, Object>>> getScheduleBookings(
            @PathVariable Long scheduleId,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Integer userType = (Integer) request.getAttribute("userType");

            if (userType == null || userType != 2) {
                return ApiResponse.error("无权限访问");
            }

            // 获取排课信息
            CoachSchedule schedule = coachScheduleMapper.selectById(scheduleId);
            if (schedule == null) {
                return ApiResponse.error("排课不存在");
            }

            // 获取该课程的预约
            LocalDateTime classTimeStart = schedule.getScheduleDate().atTime(schedule.getStartTime());
            LocalDateTime classTimeEnd = schedule.getScheduleDate().atTime(schedule.getEndTime());

            List<CourseBooking> bookings = courseBookingMapper.selectList(
                new LambdaQueryWrapper<CourseBooking>()
                    .eq(CourseBooking::getCoachId, schedule.getCoachId())
                    .ge(CourseBooking::getClassTime, classTimeStart)
                    .le(CourseBooking::getClassTime, classTimeEnd)
            );

            // 获取会员信息
            List<Long> memberIds = bookings.stream()
                .map(CourseBooking::getMemberId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

            Map<Long, MemberInfo> memberMap = new HashMap<>();
            if (!memberIds.isEmpty()) {
                List<MemberInfo> members = memberInfoMapper.selectBatchIds(memberIds);
                memberMap = members.stream()
                    .collect(Collectors.toMap(MemberInfo::getMemberId, m -> m, (a, b) -> a));
            }

            // 组装返回数据
            List<Map<String, Object>> result = new ArrayList<>();
            for (CourseBooking booking : bookings) {
                Map<String, Object> item = new HashMap<>();
                item.put("bookingId", booking.getBookingId());
                item.put("memberId", booking.getMemberId());
                item.put("memberName", booking.getMemberId() != null && memberMap.get(booking.getMemberId()) != null
                    ? memberMap.get(booking.getMemberId()).getMemberName() : "未知");
                item.put("bookingTime", booking.getBookingTime() != null
                    ? booking.getBookingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "");
                item.put("status", booking.getStatus());
                item.put("signinTime", booking.getSigninTime() != null
                    ? booking.getSigninTime().format(DateTimeFormatter.ofPattern("HH:mm")) : null);
                result.add(item);
            }

            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取预约详情失败：" + e.getMessage());
        }
    }

    /**
     * 学员签到
     */
    @PutMapping("/schedule/signin/{bookingId}")
    public ApiResponse<String> signin(@PathVariable Long bookingId, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Integer userType = (Integer) request.getAttribute("userType");

            if (userType == null || userType != 2) {
                return ApiResponse.error("无权限访问");
            }

            CourseBooking booking = courseBookingMapper.selectById(bookingId);
            if (booking == null) {
                return ApiResponse.error("预约不存在");
            }

            if ("已签到".equals(booking.getStatus())) {
                return ApiResponse.error("该学员已签到");
            }

            booking.setStatus("已签到");
            booking.setSigninTime(java.time.LocalDateTime.now());
            courseBookingMapper.updateById(booking);

            return ApiResponse.success("签到成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("签到失败：" + e.getMessage());
        }
    }

    /**
     * 开始课程
     */
    @PutMapping("/schedule/start/{scheduleId}")
    public ApiResponse<String> startCourse(@PathVariable Long scheduleId, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Integer userType = (Integer) request.getAttribute("userType");

            if (userType == null || userType != 2) {
                return ApiResponse.error("无权限访问");
            }

            CoachSchedule schedule = coachScheduleMapper.selectById(scheduleId);
            if (schedule == null) {
                return ApiResponse.error("排课不存在");
            }

            // 更新排课状态为进行中
            schedule.setStatus(2); // 进行中
            coachScheduleMapper.updateById(schedule);

            return ApiResponse.success("课程已开始");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 结束课程
     */
    @PutMapping("/schedule/end/{scheduleId}")
    public ApiResponse<String> endCourse(@PathVariable Long scheduleId, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Integer userType = (Integer) request.getAttribute("userType");

            if (userType == null || userType != 2) {
                return ApiResponse.error("无权限访问");
            }

            CoachSchedule schedule = coachScheduleMapper.selectById(scheduleId);
            if (schedule == null) {
                return ApiResponse.error("排课不存在");
            }

            // 更新排课状态为已完成
            schedule.setStatus(3); // 已完成
            coachScheduleMapper.updateById(schedule);

            // 将所有已签到的预约标记为已完成
            LocalDateTime classTimeStart = schedule.getScheduleDate().atTime(schedule.getStartTime());
            LocalDateTime classTimeEnd = schedule.getScheduleDate().atTime(schedule.getEndTime());

            List<CourseBooking> bookings = courseBookingMapper.selectList(
                new LambdaQueryWrapper<CourseBooking>()
                    .eq(CourseBooking::getCoachId, schedule.getCoachId())
                    .ge(CourseBooking::getClassTime, classTimeStart)
                    .le(CourseBooking::getClassTime, classTimeEnd)
            );

            for (CourseBooking booking : bookings) {
                if ("已签到".equals(booking.getStatus())) {
                    booking.setStatus("已完成");
                    courseBookingMapper.updateById(booking);
                }
            }

            return ApiResponse.success("课程已结束");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("操作失败：" + e.getMessage());
        }
    }
}
