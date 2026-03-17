package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CoachInfo;
import com.gym.entity.CourseBooking;
import com.gym.entity.CourseCategory;
import com.gym.entity.CourseInfo;
import com.gym.entity.MemberInfo;
import com.gym.mapper.CoachInfoMapper;
import com.gym.mapper.CourseBookingMapper;
import com.gym.mapper.CourseCategoryMapper;
import com.gym.mapper.CourseInfoMapper;
import com.gym.mapper.MemberInfoMapper;
import com.gym.service.CourseBookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseBookingServiceImpl extends ServiceImpl<CourseBookingMapper, CourseBooking> implements CourseBookingService {

    private final MemberInfoMapper memberInfoMapper;
    private final CourseInfoMapper courseInfoMapper;
    private final CoachInfoMapper coachInfoMapper;
    private final CourseCategoryMapper courseCategoryMapper;

    public CourseBookingServiceImpl(MemberInfoMapper memberInfoMapper,
                                   CourseInfoMapper courseInfoMapper,
                                   CoachInfoMapper coachInfoMapper,
                                   CourseCategoryMapper courseCategoryMapper) {
        this.memberInfoMapper = memberInfoMapper;
        this.courseInfoMapper = courseInfoMapper;
        this.coachInfoMapper = coachInfoMapper;
        this.courseCategoryMapper = courseCategoryMapper;
    }

    @Override
    public Page<CourseBooking> getPageWithDetails(int pageNum, int pageSize, CourseBooking query) {
        Page<CourseBooking> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseBooking> wrapper = buildQueryWrapper(query);
        Page<CourseBooking> result = this.page(page, wrapper);
        fillBookingDetails(result.getRecords());
        return result;
    }

    @Override
    public List<CourseBooking> getListWithDetails(CourseBooking query) {
        LambdaQueryWrapper<CourseBooking> wrapper = buildQueryWrapper(query);
        List<CourseBooking> list = this.list(wrapper);
        fillBookingDetails(list);
        return list;
    }

    private LambdaQueryWrapper<CourseBooking> buildQueryWrapper(CourseBooking query) {
        LambdaQueryWrapper<CourseBooking> wrapper = new LambdaQueryWrapper<>();
        // 显式添加 deleted = 0 条件，防止逻辑删除不生效
        wrapper.eq(CourseBooking::getDeleted, 0);
        if (query != null) {
            wrapper.eq(query.getMemberId() != null, CourseBooking::getMemberId, query.getMemberId())
                   .eq(query.getCourseId() != null, CourseBooking::getCourseId, query.getCourseId())
                   .eq(query.getCoachId() != null, CourseBooking::getCoachId, query.getCoachId())
                   .eq(StringUtils.hasText(query.getStatus()), CourseBooking::getStatus, query.getStatus())
                   .ge(query.getBookingTime() != null, CourseBooking::getBookingTime, query.getBookingTime())
                   .le(query.getClassTime() != null, CourseBooking::getClassTime, query.getClassTime());
        }
        wrapper.orderByDesc(CourseBooking::getBookingTime);
        return wrapper;
    }

    private void fillBookingDetails(List<CourseBooking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return;
        }

        // 获取所有会员ID
        List<Long> memberIds = bookings.stream()
                .map(CourseBooking::getMemberId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // 获取所有课程ID
        List<Long> courseIds = bookings.stream()
                .map(CourseBooking::getCourseId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // 获取所有教练ID
        List<Long> coachIds = bookings.stream()
                .map(CourseBooking::getCoachId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // 查询会员信息
        if (!memberIds.isEmpty()) {
            LambdaQueryWrapper<MemberInfo> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.in(MemberInfo::getMemberId, memberIds);
            List<MemberInfo> members = memberInfoMapper.selectList(memberWrapper);
            Map<Long, String> memberMap = members.stream()
                    .collect(Collectors.toMap(MemberInfo::getMemberId, MemberInfo::getMemberName, (a, b) -> a));
            bookings.forEach(booking -> {
                if (booking.getMemberId() != null) {
                    booking.setMemberName(memberMap.get(booking.getMemberId()));
                }
            });
        }

        // 查询课程信息
        if (!courseIds.isEmpty()) {
            LambdaQueryWrapper<CourseInfo> courseWrapper = new LambdaQueryWrapper<>();
            courseWrapper.in(CourseInfo::getCourseId, courseIds);
            List<CourseInfo> courses = courseInfoMapper.selectList(courseWrapper);
            Map<Long, String> courseMap = courses.stream()
                    .collect(Collectors.toMap(CourseInfo::getCourseId, CourseInfo::getCourseName, (a, b) -> a));
            Map<Long, String> courseTypeMap = courses.stream()
                    .collect(Collectors.toMap(CourseInfo::getCourseId, CourseInfo::getCourseType, (a, b) -> a));
            // category_id 可能为 null（历史数据/未归类课程），Collectors.toMap 不允许 value 为 null
            Map<Long, Long> categoryIdMap = courses.stream()
                    .filter(c -> c.getCategoryId() != null)
                    .collect(Collectors.toMap(CourseInfo::getCourseId, CourseInfo::getCategoryId, (a, b) -> a));

            bookings.forEach(booking -> {
                if (booking.getCourseId() != null) {
                    booking.setCourseName(courseMap.get(booking.getCourseId()));
                    booking.setCourseType(courseTypeMap.get(booking.getCourseId()));

                    Long categoryId = categoryIdMap.get(booking.getCourseId());
                    if (categoryId != null) {
                        CourseCategory category = courseCategoryMapper.selectById(categoryId);
                        if (category != null) {
                            booking.setCategoryName(category.getCategoryName());
                        }
                    }
                }
            });
        }

        // 查询教练信息
        if (!coachIds.isEmpty()) {
            LambdaQueryWrapper<CoachInfo> coachWrapper = new LambdaQueryWrapper<>();
            coachWrapper.in(CoachInfo::getCoachId, coachIds);
            List<CoachInfo> coaches = coachInfoMapper.selectList(coachWrapper);
            Map<Long, String> coachMap = coaches.stream()
                    .collect(Collectors.toMap(CoachInfo::getCoachId, CoachInfo::getCoachName, (a, b) -> a));
            bookings.forEach(booking -> {
                if (booking.getCoachId() != null) {
                    booking.setCoachName(coachMap.get(booking.getCoachId()));
                }
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveBooking(Long bookingId) {
        CourseBooking booking = this.getById(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!"待审核".equals(booking.getStatus())) {
            throw new RuntimeException("只有待审核状态的预约可以审核");
        }
        booking.setStatus("已预约");
        return this.updateById(booking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectBooking(Long bookingId, String reason) {
        CourseBooking booking = this.getById(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!"待审核".equals(booking.getStatus())) {
            throw new RuntimeException("只有待审核状态的预约可以拒绝");
        }
        booking.setStatus("已拒绝");
        booking.setRemark(reason);
        if (this.updateById(booking)) {
            // 释放课程容量
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signIn(Long bookingId) {
        CourseBooking booking = this.getById(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!"已预约".equals(booking.getStatus())) {
            throw new RuntimeException("只有已预约状态的预约可以签到");
        }
        booking.setStatus("已签到");
        booking.setSigninTime(LocalDateTime.now());
        return this.updateById(booking);
    }
}
