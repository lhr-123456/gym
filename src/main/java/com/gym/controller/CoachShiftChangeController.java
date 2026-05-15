package com.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachShiftChange;
import com.gym.entity.CoachSchedule;
import com.gym.entity.CourseBooking;
import com.gym.service.CoachShiftChangeService;
import com.gym.service.CoachScheduleService;
import com.gym.service.MemberMessageService;
import com.gym.mapper.CourseBookingMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/coach-shift")
public class CoachShiftChangeController {

    private final CoachShiftChangeService coachShiftChangeService;
    private final CoachScheduleService coachScheduleService;
    private final CourseBookingMapper courseBookingMapper;
    private final MemberMessageService memberMessageService;

    public CoachShiftChangeController(CoachShiftChangeService coachShiftChangeService,
                                     CoachScheduleService coachScheduleService,
                                     CourseBookingMapper courseBookingMapper,
                                     MemberMessageService memberMessageService) {
        this.coachShiftChangeService = coachShiftChangeService;
        this.coachScheduleService = coachScheduleService;
        this.courseBookingMapper = courseBookingMapper;
        this.memberMessageService = memberMessageService;
    }

    /**
     * 分页查询调班申请
     */
    @GetMapping("/page")
    public ApiResponse<Page<CoachShiftChange>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CoachShiftChange coachShiftChange) {
        Page<CoachShiftChange> page = new Page<>(pageNum, pageSize);
        Page<CoachShiftChange> result = coachShiftChangeService.page(page);
        return ApiResponse.success(result);
    }

    /**
     * 查询调班申请列表
     */
    @GetMapping("/list")
    public ApiResponse<List<CoachShiftChange>> getList(CoachShiftChange coachShiftChange) {
        List<CoachShiftChange> list = coachShiftChangeService.list();
        return ApiResponse.success(list);
    }

    /**
     * 根据ID查询调班申请
     */
    @GetMapping("/{id}")
    public ApiResponse<CoachShiftChange> getById(@PathVariable Long id) {
        CoachShiftChange coachShiftChange = coachShiftChangeService.getById(id);
        if (coachShiftChange == null) {
            return ApiResponse.error("调班不存在");
        }
        return ApiResponse.success(coachShiftChange);
    }

    /**
     * 提交调班申请
     */
    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachShiftChange coachShiftChange) {
        boolean result = coachShiftChangeService.save(coachShiftChange);
        if (result) {
            return ApiResponse.success("调班申请已提交，等待审批");
        }
        return ApiResponse.error("提交失败");
    }

    /**
     * 更新调班申请（审批通过时同步更新排班并通知会员）
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachShiftChange coachShiftChange) {
        CoachShiftChange old = coachShiftChangeService.getById(coachShiftChange.getShiftId());
        boolean result = coachShiftChangeService.updateById(coachShiftChange);
        if (!result) return ApiResponse.error("更新失败");

        // 审批通过（status=1）时，同步更新排班时间并通知会员
        if (coachShiftChange.getStatus() == 1 && old != null && old.getOriginalScheduleId() != null) {
            CoachSchedule schedule = coachScheduleService.getById(old.getOriginalScheduleId());
            if (schedule != null) {
                if (coachShiftChange.getTargetDate() != null) {
                    schedule.setScheduleDate(coachShiftChange.getTargetDate());
                }
                if (coachShiftChange.getTargetStartTime() != null) {
                    schedule.setStartTime(coachShiftChange.getTargetStartTime());
                }
                if (coachShiftChange.getTargetEndTime() != null) {
                    schedule.setEndTime(coachShiftChange.getTargetEndTime());
                }
                coachScheduleService.updateById(schedule);
                notifyAffectedMembers(schedule, "您预约的课程因教练调班已调整时间，请注意查看。");
            }
        }
        return ApiResponse.success("更新成功");
    }

    /**
     * 删除调班申请
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = coachShiftChangeService.removeById(id);
        if (result) {
            return ApiResponse.success("删除成功");
        }
        return ApiResponse.error("删除失败");
    }

    private void notifyAffectedMembers(CoachSchedule schedule, String content) {
        if (schedule == null || schedule.getScheduleId() == null) return;
        if (schedule.getCoachId() == null || schedule.getCourseId() == null) return;

        LocalDateTime dayStart = schedule.getScheduleDate().atStartOfDay();
        LocalDateTime dayEnd = schedule.getScheduleDate().atTime(23, 59, 59);

        LambdaQueryWrapper<CourseBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseBooking::getCoachId, schedule.getCoachId())
               .eq(CourseBooking::getCourseId, schedule.getCourseId())
               .ge(CourseBooking::getClassTime, dayStart)
               .le(CourseBooking::getClassTime, dayEnd)
               .ne(CourseBooking::getStatus, "已取消");

        List<CourseBooking> bookings = courseBookingMapper.selectList(wrapper);
        String title = "课程[" + (schedule.getCourseName() != null ? schedule.getCourseName() : "课程") + "]调班通知";
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
