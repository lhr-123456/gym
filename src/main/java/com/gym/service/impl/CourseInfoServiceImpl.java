package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CourseBooking;
import com.gym.entity.CourseInfo;
import com.gym.mapper.CourseBookingMapper;
import com.gym.mapper.CourseInfoMapper;
import com.gym.service.CourseInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseInfoServiceImpl implements CourseInfoService {

    private final CourseInfoMapper courseInfoMapper;
    private final CourseBookingMapper courseBookingMapper;

    public CourseInfoServiceImpl(CourseInfoMapper courseInfoMapper, 
                                CourseBookingMapper courseBookingMapper) {
        this.courseInfoMapper = courseInfoMapper;
        this.courseBookingMapper = courseBookingMapper;
    }

    @Override
    public Page<CourseInfo> getCoursePage(int pageNum, int pageSize, CourseInfo courseInfo) {
        Page<CourseInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseInfo> wrapper = buildQueryWrapper(courseInfo);
        return courseInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<CourseInfo> getAvailableCourses(int pageNum, int pageSize, CourseInfo courseInfo) {
        Page<CourseInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseInfo> wrapper = new LambdaQueryWrapper<>();

        // 只查询状态为0（正常）且未满员的课程
        wrapper.eq(CourseInfo::getStatus, 0)
               .apply("current_capacity < max_capacity");

        if (courseInfo != null) {
            wrapper.like(StringUtils.hasText(courseInfo.getCourseName()), CourseInfo::getCourseName, courseInfo.getCourseName())
                   .eq(StringUtils.hasText(courseInfo.getCourseType()), CourseInfo::getCourseType, courseInfo.getCourseType());
        }

        wrapper.orderByAsc(CourseInfo::getStartTime);
        return courseInfoMapper.selectPage(page, wrapper);
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
        return courseInfoMapper.selectById(courseId);
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
        return courseInfoMapper.selectList(wrapper);
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
        
        if (courseInfo.getCurrentCapacity() >= courseInfo.getMaxCapacity()) {
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
            if (courseInfo != null && courseInfo.getCurrentCapacity() > 0) {
                courseInfo.setCurrentCapacity(courseInfo.getCurrentCapacity() - 1);
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
                   .eq(courseInfo.getStatus() != null, CourseInfo::getStatus, courseInfo.getStatus())
                   .ge(courseInfo.getStartTime() != null, CourseInfo::getStartTime, courseInfo.getStartTime())
                   .le(courseInfo.getEndTime() != null, CourseInfo::getEndTime, courseInfo.getEndTime());
        }
        
        wrapper.orderByDesc(CourseInfo::getStartTime);
        return wrapper;
    }
}
