package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachSchedule;
import com.gym.entity.CourseBooking;
import com.gym.mapper.CourseBookingMapper;
import com.gym.service.CoachScheduleService;
import com.gym.service.MemberMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 教练排班 Controller（教练端 + 会员端共用）
 * 排班创建/变更时自动通知已预约会员
 */
@RestController
@RequestMapping("/coach-schedule")
public class CoachScheduleController {

    private final CoachScheduleService coachScheduleService;
    private final CourseBookingMapper courseBookingMapper;
    private final MemberMessageService memberMessageService;

    public CoachScheduleController(CoachScheduleService coachScheduleService,
                                  CourseBookingMapper courseBookingMapper,
                                  MemberMessageService memberMessageService) {
        this.coachScheduleService = coachScheduleService;
        this.courseBookingMapper = courseBookingMapper;
        this.memberMessageService = memberMessageService;
    }

    /**
     * 分页查询排班信息
     */
    @GetMapping("/page")
    public ApiResponse<Page<CoachSchedule>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CoachSchedule coachSchedule) {
        Page<CoachSchedule> page = new Page<>(pageNum, pageSize);
        Page<CoachSchedule> result = coachScheduleService.page(page);
        return ApiResponse.success(result);
    }

    /**
     * 根据条件查询排班列表
     */
    @GetMapping("/list")
    public ApiResponse<List<CoachSchedule>> getList(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long coachId) {
        List<CoachSchedule> list = coachScheduleService.getScheduleList(startDate, endDate, coachId);
        return ApiResponse.success(list);
    }

    /**
     * 会员端可预约课程：返回排班表数据（未来7天内、状态为待上课的）
     */
    @GetMapping("/available")
    public ApiResponse<List<CoachSchedule>> getAvailable(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LocalDate from = startDate != null ? startDate : LocalDate.now();
        LocalDate to = endDate != null ? endDate : LocalDate.now().plusDays(7);
        List<CoachSchedule> list = coachScheduleService.getScheduleList(from, to, null);
        return ApiResponse.success(list);
    }

    /**
     * 根据ID查询排班信息
     */
    @GetMapping("/{id}")
    public ApiResponse<CoachSchedule> getById(@PathVariable Long id) {
        CoachSchedule schedule = coachScheduleService.getById(id);
        if (schedule == null) {
            return ApiResponse.error("排班不存在");
        }
        return ApiResponse.success(schedule);
    }

    /**
     * 创建排班信息
     */
    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachSchedule coachSchedule) {
        boolean result = coachScheduleService.saveSchedule(coachSchedule);
        if (result) {
            return ApiResponse.success("排班创建成功");
        }
        return ApiResponse.error("排班创建失败");
    }

    /**
     * 更新排班信息
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachSchedule coachSchedule) {
        CoachSchedule old = coachScheduleService.getById(coachSchedule.getScheduleId());
        boolean result = coachScheduleService.updateSchedule(coachSchedule);
        if (result && old != null) {
            boolean timeChanged = !String.valueOf(old.getScheduleDate()).equals(String.valueOf(coachSchedule.getScheduleDate()))
                    || !String.valueOf(old.getStartTime()).equals(String.valueOf(coachSchedule.getStartTime()));
            if (timeChanged) {
                notifyAffectedMembers(old, "您的课程排班时间已更新，请注意查看。");
            }
        }
        return result ? ApiResponse.success("排班更新成功") : ApiResponse.error("排班更新失败");
    }

    /**
     * 删除排班信息
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        CoachSchedule schedule = coachScheduleService.getById(id);
        if (schedule != null) {
            notifyAffectedMembers(schedule, "您的课程已被取消，请知悉。");
        }
        coachScheduleService.removeById(id);
        return ApiResponse.success("排班已删除");
    }

    /**
     * 开始课程
     */
    @PutMapping("/start/{id}")
    public ApiResponse<String> start(@PathVariable Long id) {
        CoachSchedule schedule = coachScheduleService.getById(id);
        if (schedule == null) return ApiResponse.error("排班不存在");
        if (schedule.getStatus() != 1) return ApiResponse.error("只有待上课状态才能开始");
        schedule.setStatus(2);
        boolean result = coachScheduleService.updateById(schedule);
        return result ? ApiResponse.success("课程已开始") : ApiResponse.error("操作失败");
    }

    /**
     * 结束课程
     */
    @PutMapping("/end/{id}")
    public ApiResponse<String> end(@PathVariable Long id) {
        CoachSchedule schedule = coachScheduleService.getById(id);
        if (schedule == null) return ApiResponse.error("排班不存在");
        if (schedule.getStatus() != 2) return ApiResponse.error("只有进行中状态才能结束");
        schedule.setStatus(3);
        boolean result = coachScheduleService.updateById(schedule);
        return result ? ApiResponse.success("课程已结束") : ApiResponse.error("操作失败");
    }

    /**
     * 通过 coachId + courseId + 排班日期匹配预约记录，发送通知
     */
    private void notifyAffectedMembers(CoachSchedule schedule, String content) {
        if (schedule == null || schedule.getScheduleId() == null) return;
        if (schedule.getCoachId() == null || schedule.getCourseId() == null) return;

        LocalDateTime dayStart = schedule.getScheduleDate().atStartOfDay();
        LocalDateTime dayEnd = schedule.getScheduleDate().atTime(LocalTime.MAX);

        LambdaQueryWrapper<CourseBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseBooking::getCoachId, schedule.getCoachId())
               .eq(CourseBooking::getCourseId, schedule.getCourseId())
               .ge(CourseBooking::getClassTime, dayStart)
               .le(CourseBooking::getClassTime, dayEnd)
               .ne(CourseBooking::getStatus, "已取消");

        List<CourseBooking> bookings = courseBookingMapper.selectList(wrapper);
        String title = "课程[" + (schedule.getCourseName() != null ? schedule.getCourseName() : "课程") + "]提醒";
        for (CourseBooking b : bookings) {
            if (b.getMemberId() != null) {
                memberMessageService.pushMessage(
                    b.getMemberId(), "course", title, content,
                    String.valueOf(schedule.getScheduleId()), "coach_schedule"
                );
            }
        }
    }
}
