package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CoachInfo;
import com.gym.entity.CourseBooking;
import com.gym.entity.CourseCategory;
import com.gym.entity.CourseInfo;
import com.gym.mapper.CoachInfoMapper;
import com.gym.mapper.CourseBookingMapper;
import com.gym.mapper.CourseCategoryMapper;
import com.gym.mapper.CourseInfoMapper;
import com.gym.service.CourseInfoService;
import com.gym.service.CourseReminderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseInfoServiceImpl implements CourseInfoService {

    private final CourseInfoMapper courseInfoMapper;
    private final CourseBookingMapper courseBookingMapper;
    private final CoachInfoMapper coachInfoMapper;
    private final CourseCategoryMapper courseCategoryMapper;
    private final CourseReminderService reminderService;

    public CourseInfoServiceImpl(CourseInfoMapper courseInfoMapper,
                                CourseBookingMapper courseBookingMapper,
                                CoachInfoMapper coachInfoMapper,
                                CourseCategoryMapper courseCategoryMapper,
                                CourseReminderService reminderService) {
        this.courseInfoMapper = courseInfoMapper;
        this.courseBookingMapper = courseBookingMapper;
        this.coachInfoMapper = coachInfoMapper;
        this.courseCategoryMapper = courseCategoryMapper;
        this.reminderService = reminderService;
    }

    @Override
    public Page<CourseInfo> getCoursePage(int pageNum, int pageSize, CourseInfo courseInfo) {
        Page<CourseInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseInfo> wrapper = buildQueryWrapper(courseInfo);
        Page<CourseInfo> result = courseInfoMapper.selectPage(page, wrapper);
        // 填充教练名称和分类名称
        fillCourseNames(result.getRecords());
        return result;
    }

    @Override
    public Page<CourseInfo> getAvailableCourses(int pageNum, int pageSize, CourseInfo courseInfo) {
        Page<CourseInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseInfo> wrapper = new LambdaQueryWrapper<>();

        // 只查询状态为0（正常）且未满员的课程（以实际预约记录为准，避免 dangqian_renshu 与预约表不一致）
        wrapper.eq(CourseInfo::getStatus, 0)
               .apply("(SELECT COUNT(*) FROM course_booking WHERE kecheng_id = course_info.kecheng_id AND deleted = 0 AND NOT (zhuangtai <=> '已取消')) < zuida_renshu");

        if (courseInfo != null) {
            wrapper.like(StringUtils.hasText(courseInfo.getCourseName()), CourseInfo::getCourseName, courseInfo.getCourseName())
                   .eq(StringUtils.hasText(courseInfo.getCourseType()), CourseInfo::getCourseType, courseInfo.getCourseType())
                   .eq(courseInfo.getCategoryId() != null, CourseInfo::getCategoryId, courseInfo.getCategoryId());
        }

        wrapper.orderByAsc(CourseInfo::getStartTime);
        Page<CourseInfo> result = courseInfoMapper.selectPage(page, wrapper);
        fillCourseNames(result.getRecords());
        return result;
    }

    @Override
    public List<CourseBooking> getCourseBookings(Long courseId) {
        LambdaQueryWrapper<CourseBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseBooking::getCourseId, courseId)
               .orderByDesc(CourseBooking::getBookingTime);
        return courseBookingMapper.selectList(wrapper);
    }

    @Override
    public CourseInfo getById(Long courseId) {
        CourseInfo courseInfo = courseInfoMapper.selectById(courseId);
        if (courseInfo != null) {
            fillCourseNames(Collections.singletonList(courseInfo));
        }
        return courseInfo;
    }

    @Override
    public boolean save(CourseInfo courseInfo) {
        if (courseInfo.getCurrentCapacity() == null) {
            courseInfo.setCurrentCapacity(0);
        }
        if (courseInfo.getStatus() == null) {
            courseInfo.setStatus(0);
        }
        return courseInfoMapper.insert(courseInfo) > 0;
    }

    @Override
    @Transactional
    public boolean updateById(CourseInfo courseInfo) {
        return courseInfoMapper.updateById(courseInfo) > 0;
    }

    @Override
    public boolean deleteById(Long courseId) {
        return courseInfoMapper.deleteById(courseId) > 0;
    }

    @Override
    public List<CourseInfo> list(CourseInfo courseInfo) {
        LambdaQueryWrapper<CourseInfo> wrapper = buildQueryWrapper(courseInfo);
        List<CourseInfo> result = courseInfoMapper.selectList(wrapper);
        fillCourseNames(result);
        return result;
    }

    /**
     * 统计某课程当前有效预约人数（不含已取消）
     */
    private int countActiveBookings(Long courseId) {
        LambdaQueryWrapper<CourseBooking> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseBooking::getCourseId, courseId)
                .apply("NOT (status <=> {0})", "已取消");
        Long c = courseBookingMapper.selectCount(wrapper);
        return c == null ? 0 : c.intValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bookCourse(Long courseId, Long memberId, Long coachId) {
        CourseInfo courseInfo = courseInfoMapper.selectById(courseId);
        if (courseInfo == null) {
            throw new RuntimeException("课程不存在");
        }

        if (courseInfo.getStatus() != 0) {
            throw new RuntimeException("课程已取消或已满员");
        }

        // 同一会员不可重复预约同一门未取消的课程
        LambdaQueryWrapper<CourseBooking> dup = new LambdaQueryWrapper<>();
        dup.eq(CourseBooking::getCourseId, courseId)
                .eq(CourseBooking::getMemberId, memberId)
                .apply("NOT (status <=> {0})", "已取消");
        if (courseBookingMapper.selectCount(dup) > 0) {
            throw new RuntimeException("您已预约该课程，无需重复兑换");
        }

        // 以预约表实际人数为准同步 current_capacity，避免与列表展示不一致
        int actualBookings = countActiveBookings(courseId);
        courseInfo.setCurrentCapacity(actualBookings);
        courseInfoMapper.updateById(courseInfo);

        Integer maxCap = courseInfo.getMaxCapacity();
        if (maxCap == null || maxCap <= 0) {
            throw new RuntimeException("课程设置异常，无法预约");
        }

        if (actualBookings >= maxCap) {
            courseInfo.setStatus(2);
            courseInfoMapper.updateById(courseInfo);
            throw new RuntimeException("课程已满员");
        }

        CourseBooking booking = new CourseBooking();
        booking.setMemberId(memberId);
        booking.setCourseId(courseId);
        booking.setCoachId(coachId);
        booking.setBookingTime(LocalDateTime.now());
        booking.setClassTime(courseInfo.getStartTime());
        booking.setStatus("已预约");

        if (courseBookingMapper.insert(booking) > 0) {
            courseInfo.setCurrentCapacity(courseInfo.getCurrentCapacity() + 1);
            if (courseInfo.getCurrentCapacity() >= courseInfo.getMaxCapacity()) {
                courseInfo.setStatus(2);
            }
            courseInfoMapper.updateById(courseInfo);

            // 创建课程提醒（开课前1小时）
            String coachName = null;
            if (coachId != null) {
                CoachInfo coach = coachInfoMapper.selectById(coachId);
                if (coach != null) coachName = coach.getCoachName();
            }
            reminderService.createReminder(
                memberId,
                booking.getBookingId(),
                courseId,
                courseInfo.getCourseName(),
                coachName,
                courseInfo.getStartTime(),
                courseInfo.getRoom()
            );

            return true;
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelBooking(Long bookingId) {
        CourseBooking booking = courseBookingMapper.selectById(bookingId);
        if (booking == null || "已取消".equals(booking.getStatus())) {
            throw new RuntimeException("预约不存在或已取消");
        }

        booking.setStatus("已取消");
        if (courseBookingMapper.updateById(booking) > 0) {
            CourseInfo courseInfo = courseInfoMapper.selectById(booking.getCourseId());
            if (courseInfo != null) {
                // 以预约表实际人数为准重新同步
                courseInfo.setCurrentCapacity(countActiveBookings(booking.getCourseId()));
                if (courseInfo.getStatus() == 2) {
                    courseInfo.setStatus(0);
                }
                courseInfoMapper.updateById(courseInfo);
            }
            return true;
        }

        return false;
    }

    private LambdaQueryWrapper<CourseInfo> buildQueryWrapper(CourseInfo courseInfo) {
        LambdaQueryWrapper<CourseInfo> wrapper = new LambdaQueryWrapper<>();

        if (courseInfo != null) {
            wrapper.eq(courseInfo.getCourseId() != null, CourseInfo::getCourseId, courseInfo.getCourseId())
                   .like(StringUtils.hasText(courseInfo.getCourseName()), CourseInfo::getCourseName, courseInfo.getCourseName())
                   .eq(courseInfo.getCoachId() != null, CourseInfo::getCoachId, courseInfo.getCoachId())
                   .eq(StringUtils.hasText(courseInfo.getCourseType()), CourseInfo::getCourseType, courseInfo.getCourseType())
                   .eq(courseInfo.getCategoryId() != null, CourseInfo::getCategoryId, courseInfo.getCategoryId())
                   .eq(courseInfo.getStatus() != null, CourseInfo::getStatus, courseInfo.getStatus())
                   .ge(courseInfo.getStartTime() != null, CourseInfo::getStartTime, courseInfo.getStartTime())
                   .le(courseInfo.getEndTime() != null, CourseInfo::getStartTime, courseInfo.getEndTime());
        }

        wrapper.orderByDesc(CourseInfo::getStartTime);
        return wrapper;
    }

    private void fillCourseNames(List<CourseInfo> courses) {
        if (courses == null || courses.isEmpty()) {
            return;
        }

        // 获取所有教练ID
        List<Long> coachIds = courses.stream()
                .map(CourseInfo::getCoachId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // 获取所有分类ID
        List<Long> categoryIds = courses.stream()
                .map(CourseInfo::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // 查询教练信息
        if (!coachIds.isEmpty()) {
            LambdaQueryWrapper<CoachInfo> coachWrapper = new LambdaQueryWrapper<>();
            coachWrapper.in(CoachInfo::getCoachId, coachIds);
            List<CoachInfo> coaches = coachInfoMapper.selectList(coachWrapper);

            // 构建教练ID到名称的映射
            Map<Long, String> coachMap = coaches.stream()
                    .collect(Collectors.toMap(CoachInfo::getCoachId, CoachInfo::getCoachName, (a, b) -> a));
            courses.forEach(course -> {
                if (course.getCoachId() != null) {
                    course.setCoachName(coachMap.get(course.getCoachId()));
                }
            });
        }

        // 查询分类信息
        if (!categoryIds.isEmpty()) {
            LambdaQueryWrapper<CourseCategory> categoryWrapper = new LambdaQueryWrapper<>();
            categoryWrapper.in(CourseCategory::getCategoryId, categoryIds);
            List<CourseCategory> categories = courseCategoryMapper.selectList(categoryWrapper);

            // 构建分类ID到名称的映射
            Map<Long, String> categoryMap = categories.stream()
                    .collect(Collectors.toMap(CourseCategory::getCategoryId, CourseCategory::getCategoryName, (a, b) -> a));
            courses.forEach(course -> {
                if (course.getCategoryId() != null) {
                    course.setCategoryName(categoryMap.get(course.getCategoryId()));
                }
            });
        }
    }
}
