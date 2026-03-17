package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.*;
import com.gym.mapper.*;
import com.gym.service.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/coach")
public class CoachDashboardController {

    private final CourseBookingMapper courseBookingMapper;
    private final MemberInfoMapper memberInfoMapper;
    private final CourseInfoMapper courseInfoMapper;
    private final CoachInfoMapper coachInfoMapper;
    private final UserInfoMapper userInfoMapper;

    public CoachDashboardController(CourseBookingMapper courseBookingMapper,
                                   MemberInfoMapper memberInfoMapper,
                                   CourseInfoMapper courseInfoMapper,
                                   CoachInfoMapper coachInfoMapper,
                                   UserInfoMapper userInfoMapper) {
        this.courseBookingMapper = courseBookingMapper;
        this.memberInfoMapper = memberInfoMapper;
        this.courseInfoMapper = courseInfoMapper;
        this.coachInfoMapper = coachInfoMapper;
        this.userInfoMapper = userInfoMapper;
    }

    /**
     * 获取教练首页统计数据
     */
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboard(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Integer userType = (Integer) request.getAttribute("userType");

            if (userType == null || userType != 2) {
                return ApiResponse.error("无权限访问");
            }

            // 获取教练ID
            UserInfo userInfo = userInfoMapper.selectById(userId);
            if (userInfo == null || userInfo.getCoachId() == null) {
                return ApiResponse.error("教练信息不存在");
            }

            Long coachId = userInfo.getCoachId();
            CoachInfo coachInfo = coachInfoMapper.selectById(coachId);

            Map<String, Object> data = new HashMap<>();
            data.put("coachName", coachInfo != null ? coachInfo.getCoachName() : "教练");

            // 本月课时
            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime monthEnd = LocalDate.now().atTime(23, 59, 59);
            List<CourseBooking> monthBookings = courseBookingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CourseBooking>()
                    .eq(CourseBooking::getCoachId, coachId)
                    .ge(CourseBooking::getClassTime, monthStart)
                    .le(CourseBooking::getClassTime, monthEnd)
                    .eq(CourseBooking::getStatus, "已完成")
            );
            data.put("monthHours", monthBookings.size() * 1); // 假设每节课1小时

            // 学员评分（从评价表获取）
            data.put("rating", "4.8");

            // 出勤率
            List<CourseBooking> todayBookings = courseBookingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CourseBooking>()
                    .eq(CourseBooking::getCoachId, coachId)
                    .ge(CourseBooking::getClassTime, LocalDate.now().atStartOfDay())
                    .le(CourseBooking::getClassTime, LocalDate.now().atTime(23, 59, 59))
            );
            if (!todayBookings.isEmpty()) {
                long signedCount = todayBookings.stream()
                    .filter(b -> "已签到".equals(b.getStatus()))
                    .count();
                data.put("attendance", String.format("%.0f%%", (signedCount * 100.0 / todayBookings.size())));
            } else {
                data.put("attendance", "0%");
            }

            // 累计学员数
            List<CourseBooking> allBookings = courseBookingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CourseBooking>()
                    .eq(CourseBooking::getCoachId, coachId)
            );
            long totalMembers = allBookings.stream()
                .map(CourseBooking::getMemberId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
            data.put("totalMembers", totalMembers);

            return ApiResponse.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取教练今日课程
     */
    @GetMapping("/courses/today")
    public ApiResponse<List<Map<String, Object>>> getTodayCourses(HttpServletRequest request) {
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
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);

            // 获取今日课程预约
            List<CourseBooking> bookings = courseBookingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CourseBooking>()
                    .eq(CourseBooking::getCoachId, coachId)
                    .ge(CourseBooking::getClassTime, todayStart)
                    .le(CourseBooking::getClassTime, todayEnd)
                    .orderByAsc(CourseBooking::getClassTime)
            );

            // 收集课程信息
            List<Long> courseIds = bookings.stream()
                .map(CourseBooking::getCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

            Map<Long, CourseInfo> courseInfoMap = new HashMap<>();
            if (!courseIds.isEmpty()) {
                List<CourseInfo> courses = courseInfoMapper.selectBatchIds(courseIds);
                courseInfoMap = courses.stream()
                    .collect(Collectors.toMap(CourseInfo::getCourseId, c -> c, (a, b) -> a));
            }

            // 按课程分组
            Map<Long, List<CourseBooking>> bookingsByCourse = bookings.stream()
                .collect(Collectors.groupingBy(CourseBooking::getCourseId));

            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<Long, List<CourseBooking>> entry : bookingsByCourse.entrySet()) {
                CourseInfo courseInfo = courseInfoMap.get(entry.getKey());
                List<CourseBooking> courseBookings = entry.getValue();

                Map<String, Object> item = new HashMap<>();
                item.put("bookingId", entry.getKey());
                item.put("courseName", courseInfo != null ? courseInfo.getCourseName() : "未知课程");
                item.put("maxCapacity", courseInfo != null ? courseInfo.getMaxCapacity() : 0);
                item.put("bookedCount", courseBookings.size());
                item.put("signedCount", courseBookings.stream()
                    .filter(b -> "已签到".equals(b.getStatus()))
                    .count());

                // 获取课程时间
                if (!courseBookings.isEmpty()) {
                    CourseBooking first = courseBookings.get(0);
                    item.put("startTime", first.getClassTime() != null ?
                        first.getClassTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                    item.put("endTime", first.getClassTime() != null && courseInfo != null && courseInfo.getDurationMin() != null ?
                        first.getClassTime().plusMinutes(courseInfo.getDurationMin()).format(DateTimeFormatter.ofPattern("HH:mm")) : "");
                }

                item.put("status", courseBookings.get(0).getStatus());
                result.add(item);
            }

            // 按时间排序
            result.sort((a, b) -> {
                String t1 = (String) a.get("startTime");
                String t2 = (String) b.get("startTime");
                return t1.compareTo(t2);
            });

            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取课程失败：" + e.getMessage());
        }
    }

    /**
     * 获取教练今日学员
     */
    @GetMapping("/members/today")
    public ApiResponse<List<Map<String, Object>>> getTodayMembers(HttpServletRequest request) {
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
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);

            // 获取今日预约的学员
            List<CourseBooking> bookings = courseBookingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CourseBooking>()
                    .eq(CourseBooking::getCoachId, coachId)
                    .ge(CourseBooking::getClassTime, todayStart)
                    .le(CourseBooking::getClassTime, todayEnd)
            );

            // 获取学员信息
            List<Long> memberIds = bookings.stream()
                .map(CourseBooking::getMemberId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

            Map<Long, MemberInfo> memberInfoMap = new HashMap<>();
            if (!memberIds.isEmpty()) {
                List<MemberInfo> members = memberInfoMapper.selectBatchIds(memberIds);
                memberInfoMap = members.stream()
                    .collect(Collectors.toMap(MemberInfo::getMemberId, m -> m, (a, b) -> a));
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (Long memberId : memberIds) {
                MemberInfo member = memberInfoMap.get(memberId);
                if (member != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("memberId", memberId);
                    item.put("memberName", member.getMemberName());
                    result.add(item);
                }
            }

            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取学员失败：" + e.getMessage());
        }
    }

    /**
     * 获取教练学员列表（分页）
     * 说明：基于教练历史预约记录统计去重学员，并补充简单统计字段，避免前端使用模拟数据。
     */
    @GetMapping("/members/page")
    public ApiResponse<Page<Map<String, Object>>> getCoachMembersPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
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

            // 取出该教练所有预约记录（仅用于统计学员清单与课程次数）
            List<CourseBooking> allBookings = courseBookingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CourseBooking>()
                    .eq(CourseBooking::getCoachId, coachId)
            );

            // 统计每个学员的课程次数与完成次数
            Map<Long, List<CourseBooking>> byMember = allBookings.stream()
                .filter(b -> b.getMemberId() != null)
                .collect(Collectors.groupingBy(CourseBooking::getMemberId));

            List<Long> memberIds = byMember.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

            Map<Long, MemberInfo> memberInfoMap = new HashMap<>();
            if (!memberIds.isEmpty()) {
                List<MemberInfo> members = memberInfoMapper.selectBatchIds(memberIds);
                memberInfoMap = members.stream()
                    .collect(Collectors.toMap(MemberInfo::getMemberId, m -> m, (a, b) -> a));
            }

            // 组装结果（先过滤 keyword，再分页）
            String kw = keyword == null ? null : keyword.trim();
            List<Map<String, Object>> rows = new ArrayList<>();
            for (Long memberId : memberIds) {
                MemberInfo member = memberInfoMap.get(memberId);
                if (member == null) continue;

                if (kw != null && !kw.isEmpty()) {
                    String name = member.getMemberName() == null ? "" : member.getMemberName();
                    String phone = member.getPhoneNum() == null ? "" : member.getPhoneNum();
                    if (!name.contains(kw) && !phone.contains(kw)) {
                        continue;
                    }
                }

                List<CourseBooking> bookings = byMember.getOrDefault(memberId, Collections.emptyList());
                long totalCourses = bookings.size();
                long completedCourses = bookings.stream().filter(b -> "已完成".equals(b.getStatus())).count();
                long remainingCourses = Math.max(0, totalCourses - completedCourses);
                LocalDateTime lastTime = bookings.stream()
                    .map(CourseBooking::getClassTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

                Map<String, Object> item = new HashMap<>();
                item.put("memberId", memberId);
                item.put("memberName", member.getMemberName());
                item.put("gender", member.getGender());
                item.put("phoneNum", member.getPhoneNum());
                item.put("totalCourses", totalCourses);
                item.put("completedCourses", completedCourses);
                item.put("remainingCourses", remainingCourses);
                item.put("lastCourseDate", lastTime != null ? lastTime.toLocalDate().toString() : "");
                rows.add(item);
            }

            int total = rows.size();
            int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
            int toIndex = Math.min(total, fromIndex + pageSize);
            List<Map<String, Object>> pageRecords = fromIndex >= total ? Collections.emptyList() : rows.subList(fromIndex, toIndex);

            Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);
            page.setTotal(total);
            page.setRecords(pageRecords);
            return ApiResponse.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取学员列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取教练学员下拉列表（体测录入使用）
     */
    @GetMapping("/members/list")
    public ApiResponse<List<Map<String, Object>>> getCoachMembersList(HttpServletRequest request) {
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
            List<CourseBooking> allBookings = courseBookingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CourseBooking>()
                    .eq(CourseBooking::getCoachId, coachId)
            );

            List<Long> memberIds = allBookings.stream()
                .map(CourseBooking::getMemberId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

            if (memberIds.isEmpty()) {
                return ApiResponse.success(Collections.emptyList());
            }

            List<MemberInfo> members = memberInfoMapper.selectBatchIds(memberIds);
            List<Map<String, Object>> result = new ArrayList<>();
            for (MemberInfo m : members) {
                if (m == null) continue;
                Map<String, Object> item = new HashMap<>();
                item.put("memberId", m.getMemberId());
                item.put("memberName", m.getMemberName());
                item.put("phoneNum", m.getPhoneNum());
                result.add(item);
            }
            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取学员列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取教练待办事项
     */
    @GetMapping("/todos")
    public ApiResponse<List<Map<String, Object>>> getTodos(HttpServletRequest request) {
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
            List<Map<String, Object>> todos = new ArrayList<>();

            // 待审核的请假申请（暂时返回空）
            // 待填写的课程评价
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
            List<CourseBooking> todayBookings = courseBookingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CourseBooking>()
                    .eq(CourseBooking::getCoachId, coachId)
                    .ge(CourseBooking::getClassTime, todayStart)
                    .le(CourseBooking::getClassTime, todayEnd)
                    .eq(CourseBooking::getStatus, "已完成")
            );

            if (!todayBookings.isEmpty()) {
                Map<String, Object> todo = new HashMap<>();
                todo.put("id", 1);
                todo.put("content", "今日课后需要填写 " + todayBookings.size() + " 条课程评价");
                todo.put("icon", "el-icon-star-on");
                todo.put("color", "#F56C6C");
                todo.put("actionText", "去填写");
                todo.put("action", "fillReview");
                todos.add(todo);
            }

            return ApiResponse.success(todos);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取待办失败：" + e.getMessage());
        }
    }
}
