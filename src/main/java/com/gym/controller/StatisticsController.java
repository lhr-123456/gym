package com.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gym.dto.ApiResponse;
import com.gym.entity.*;
import com.gym.mapper.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final MemberInfoMapper memberInfoMapper;
    private final CoachInfoMapper coachInfoMapper;
    private final CourseInfoMapper courseInfoMapper;
    private final EquipmentInfoMapper equipmentInfoMapper;
    private final CoachSalaryMapper coachSalaryMapper;
    private final CoachPerformanceMapper coachPerformanceMapper;
    private final CourseBookingMapper courseBookingMapper;
    private final CourseReviewMapper courseReviewMapper;

    public StatisticsController(MemberInfoMapper memberInfoMapper,
                              CoachInfoMapper coachInfoMapper,
                              CourseInfoMapper courseInfoMapper,
                              EquipmentInfoMapper equipmentInfoMapper,
                              CoachSalaryMapper coachSalaryMapper,
                              CoachPerformanceMapper coachPerformanceMapper,
                              CourseBookingMapper courseBookingMapper,
                              CourseReviewMapper courseReviewMapper) {
        this.memberInfoMapper = memberInfoMapper;
        this.coachInfoMapper = coachInfoMapper;
        this.courseInfoMapper = courseInfoMapper;
        this.equipmentInfoMapper = equipmentInfoMapper;
        this.coachSalaryMapper = coachSalaryMapper;
        this.coachPerformanceMapper = coachPerformanceMapper;
        this.courseBookingMapper = courseBookingMapper;
        this.courseReviewMapper = courseReviewMapper;
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboardStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 会员总数
            Long memberCount = memberInfoMapper.selectCount(null);
            statistics.put("memberCount", memberCount);

            // 教练总数
            Long coachCount = coachInfoMapper.selectCount(null);
            statistics.put("coachCount", coachCount);

            // 课程总数
            Long courseCount = courseInfoMapper.selectCount(null);
            statistics.put("courseCount", courseCount);

            // 器材总数
            Long equipmentCount = equipmentInfoMapper.selectCount(null);
            statistics.put("equipmentCount", equipmentCount);

            // 今日新增会员数
            // 可以根据需要添加更复杂的统计逻辑

            return ApiResponse.success(statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 管理员端 - 教练工资概览（用于教练工资页面顶部的统计卡片）
     * 无角色限制，管理员/教练均可访问
     */
    @GetMapping("/coach-salary-summary")
    public ApiResponse<Map<String, Object>> getCoachSalarySummary(
            @RequestParam(required = false) Long coachId) {
        try {
            Map<String, Object> summary = new HashMap<>();
            summary.put("baseSalary", 0.0);
            summary.put("monthCourseFee", 0.0);
            summary.put("monthPerformanceBonus", 0.0);
            summary.put("monthTotalSalary", 0.0);
            summary.put("monthHours", 0);
            summary.put("avgRating", null);

            // 本月课时费汇总
            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime monthEnd = LocalDate.now().atTime(23, 59, 59);

            LambdaQueryWrapper<CoachSalary> salaryWrapper = new LambdaQueryWrapper<>();
            if (coachId != null) {
                salaryWrapper.eq(CoachSalary::getCoachId, coachId);
            }
            List<CoachSalary> salaries = coachSalaryMapper.selectList(salaryWrapper);

            double totalBase = 0;
            double totalCourseFee = 0;
            double totalBonus = 0;
            double totalSalary = 0;
            for (CoachSalary s : salaries) {
                if (s.getSalaryMonth() != null && s.getSalaryMonth().equals(LocalDate.now().toString().substring(0, 7))) {
                    totalBase += s.getBaseSalary() != null ? s.getBaseSalary().doubleValue() : 0;
                    totalCourseFee += s.getClassTotal() != null ? s.getClassTotal().doubleValue() : 0;
                    totalBonus += (s.getBonus() != null ? s.getBonus().doubleValue() : 0)
                                + (s.getCommission() != null ? s.getCommission().doubleValue() : 0);
                    totalSalary += s.getTotalSalary() != null ? s.getTotalSalary().doubleValue() : 0;
                }
            }
            summary.put("baseSalary", totalBase);
            summary.put("monthCourseFee", totalCourseFee);
            summary.put("monthPerformanceBonus", totalBonus);
            summary.put("monthTotalSalary", totalSalary);

            // 本月课时数
            if (coachId != null) {
                long monthHours = courseBookingMapper.selectCount(
                    new LambdaQueryWrapper<CourseBooking>()
                        .eq(CourseBooking::getCoachId, coachId)
                        .ge(CourseBooking::getClassTime, monthStart)
                        .le(CourseBooking::getClassTime, monthEnd)
                        .eq(CourseBooking::getStatus, "已完成")
                );
                summary.put("monthHours", monthHours);

                // 本月评价均分
                List<CourseReview> reviews = courseReviewMapper.selectList(
                    new LambdaQueryWrapper<CourseReview>()
                        .eq(CourseReview::getCoachId, coachId)
                );
                double avgRating = reviews.stream()
                    .filter(r -> r.getRating() != null)
                    .mapToInt(CourseReview::getRating)
                    .average()
                    .orElse(0);
                summary.put("avgRating", avgRating > 0 ? Math.round(avgRating * 10) / 10.0 : null);
            }

            return ApiResponse.success(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取工资概览失败：" + e.getMessage());
        }
    }

    /**
     * 管理员端 - 教练绩效概览（用于教练绩效页面顶部的统计卡片）
     * 无角色限制，管理员/教练均可访问
     */
    @GetMapping("/coach-performance-summary")
    public ApiResponse<Map<String, Object>> getCoachPerformanceSummary(
            @RequestParam(required = false) Long coachId) {
        try {
            Map<String, Object> summary = new HashMap<>();
            summary.put("avgRating", null);
            summary.put("monthHours", 0);
            summary.put("attendanceRate", null);

            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime monthEnd = LocalDate.now().atTime(23, 59, 59);

            // 绩效评价均分
            if (coachId != null) {
                List<CoachPerformance> performances = coachPerformanceMapper.selectList(
                    new LambdaQueryWrapper<CoachPerformance>()
                        .eq(CoachPerformance::getCoachId, coachId)
                        .orderByDesc(CoachPerformance::getEvalTime)
                        .last("LIMIT 1")
                );
                if (!performances.isEmpty()) {
                    CoachPerformance p = performances.get(0);
                    summary.put("avgRating", p.getTotalScore());
                }

                // 本月课时数
                long monthHours = courseBookingMapper.selectCount(
                    new LambdaQueryWrapper<CourseBooking>()
                        .eq(CourseBooking::getCoachId, coachId)
                        .ge(CourseBooking::getClassTime, monthStart)
                        .le(CourseBooking::getClassTime, monthEnd)
                        .eq(CourseBooking::getStatus, "已完成")
                );
                summary.put("monthHours", monthHours);

                // 出勤率
                long totalSchedules = monthHours;
                long attended = monthHours; // 已完成即出勤
                if (totalSchedules > 0) {
                    summary.put("attendanceRate", String.format("%.0f%%", (attended * 100.0 / totalSchedules)));
                }
            }

            return ApiResponse.success(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取绩效概览失败：" + e.getMessage());
        }
    }
}
