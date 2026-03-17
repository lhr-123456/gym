package com.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachInfo;
import com.gym.entity.CoachSchedule;
import com.gym.mapper.CoachInfoMapper;
import com.gym.service.CoachScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/coach-schedule")
public class CoachScheduleController {

    private final CoachScheduleService coachScheduleService;
    private final CoachInfoMapper coachInfoMapper;

    public CoachScheduleController(CoachScheduleService coachScheduleService, CoachInfoMapper coachInfoMapper) {
        this.coachScheduleService = coachScheduleService;
        this.coachInfoMapper = coachInfoMapper;
    }

    @GetMapping("/page")
    public ApiResponse<Page<CoachSchedule>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CoachSchedule coachSchedule) {
        Page<CoachSchedule> page = new Page<>(pageNum, pageSize);
        Page<CoachSchedule> result = coachScheduleService.page(page);
        return ApiResponse.success(result);
    }

    @GetMapping("/list")
    public ApiResponse<List<CoachSchedule>> getList(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long coachId) {

        LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachSchedule::getDeleted, 0);

        if (startDate != null) {
            wrapper.ge(CoachSchedule::getScheduleDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(CoachSchedule::getScheduleDate, endDate);
        }
        if (coachId != null) {
            wrapper.eq(CoachSchedule::getCoachId, coachId);
        }

        wrapper.orderByAsc(CoachSchedule::getScheduleDate)
               .orderByAsc(CoachSchedule::getStartTime);

        List<CoachSchedule> list = coachScheduleService.list(wrapper);

        // 填充教练名称
        List<Long> coachIds = list.stream()
            .map(CoachSchedule::getCoachId)
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());

        if (!coachIds.isEmpty()) {
            List<CoachInfo> coaches = coachInfoMapper.selectBatchIds(coachIds);
            Map<Long, String> coachMap = coaches.stream()
                .collect(Collectors.toMap(CoachInfo::getCoachId, CoachInfo::getCoachName, (a, b) -> a));
            list.forEach(schedule -> {
                if (schedule.getCoachId() != null) {
                    schedule.setRemarks(coachMap.get(schedule.getCoachId()));
                }
            });
        }

        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<CoachSchedule> getById(@PathVariable Long id) {
        CoachSchedule coachSchedule = coachScheduleService.getById(id);
        if (coachSchedule == null) {
            return ApiResponse.error("排班不存在");
        }
        return ApiResponse.success(coachSchedule);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachSchedule coachSchedule) {
        boolean result = coachScheduleService.save(coachSchedule);
        if (result) {
            return ApiResponse.success("添加排班成功");
        }
        return ApiResponse.error("添加排班失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachSchedule coachSchedule) {
        boolean result = coachScheduleService.updateById(coachSchedule);
        if (result) {
            return ApiResponse.success("更新排班成功");
        }
        return ApiResponse.error("更新排班失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = coachScheduleService.removeById(id);
        if (result) {
            return ApiResponse.success("删除排班成功");
        }
        return ApiResponse.error("删除排班失败");
    }

    @PutMapping("/start/{id}")
    public ApiResponse<String> start(@PathVariable Long id) {
        try {
            CoachSchedule schedule = coachScheduleService.getById(id);
            if (schedule == null) {
                return ApiResponse.error("排班不存在");
            }
            if (schedule.getStatus() == null || schedule.getStatus() != 1) {
                return ApiResponse.error("只有待上课状态的课程才能开始");
            }
            schedule.setStatus(2);
            boolean result = coachScheduleService.updateById(schedule);
            if (result) {
                return ApiResponse.success("课程已开始");
            }
            return ApiResponse.error("开始课程失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/end/{id}")
    public ApiResponse<String> end(@PathVariable Long id) {
        try {
            CoachSchedule schedule = coachScheduleService.getById(id);
            if (schedule == null) {
                return ApiResponse.error("排班不存在");
            }
            if (schedule.getStatus() == null || schedule.getStatus() != 2) {
                return ApiResponse.error("只有进行中状态的课程才能结束");
            }
            schedule.setStatus(3);
            boolean result = coachScheduleService.updateById(schedule);
            if (result) {
                return ApiResponse.success("课程已结束");
            }
            return ApiResponse.error("结束课程失败");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
