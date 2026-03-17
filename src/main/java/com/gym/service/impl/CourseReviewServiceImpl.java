package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CoachInfo;
import com.gym.entity.CourseInfo;
import com.gym.entity.CourseReview;
import com.gym.entity.MemberInfo;
import com.gym.mapper.CoachInfoMapper;
import com.gym.mapper.CourseInfoMapper;
import com.gym.mapper.CourseReviewMapper;
import com.gym.mapper.MemberInfoMapper;
import com.gym.service.CourseReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseReviewServiceImpl implements CourseReviewService {

    private final CourseReviewMapper courseReviewMapper;
    private final MemberInfoMapper memberInfoMapper;
    private final CourseInfoMapper courseInfoMapper;
    private final CoachInfoMapper coachInfoMapper;

    public CourseReviewServiceImpl(CourseReviewMapper courseReviewMapper,
                                 MemberInfoMapper memberInfoMapper,
                                 CourseInfoMapper courseInfoMapper,
                                 CoachInfoMapper coachInfoMapper) {
        this.courseReviewMapper = courseReviewMapper;
        this.memberInfoMapper = memberInfoMapper;
        this.courseInfoMapper = courseInfoMapper;
        this.coachInfoMapper = coachInfoMapper;
    }

    @Override
    public Page<CourseReview> getPage(int pageNum, int pageSize, CourseReview courseReview) {
        Page<CourseReview> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CourseReview> wrapper = buildQueryWrapper(courseReview);
        Page<CourseReview> result = courseReviewMapper.selectPage(page, wrapper);
        fillReviewDetails(result.getRecords());
        return result;
    }

    @Override
    public List<CourseReview> getList(CourseReview courseReview) {
        LambdaQueryWrapper<CourseReview> wrapper = buildQueryWrapper(courseReview);
        List<CourseReview> list = courseReviewMapper.selectList(wrapper);
        fillReviewDetails(list);
        return list;
    }

    private LambdaQueryWrapper<CourseReview> buildQueryWrapper(CourseReview query) {
        LambdaQueryWrapper<CourseReview> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            wrapper.eq(query.getCourseId() != null, CourseReview::getCourseId, query.getCourseId())
                   .eq(query.getMemberId() != null, CourseReview::getMemberId, query.getMemberId())
                   .eq(query.getCoachId() != null, CourseReview::getCoachId, query.getCoachId())
                   .eq(query.getRating() != null, CourseReview::getRating, query.getRating())
                   .eq(query.getStatus() != null, CourseReview::getStatus, query.getStatus())
                   .like(StringUtils.hasText(query.getContent()), CourseReview::getContent, query.getContent());
        }
        wrapper.orderByDesc(CourseReview::getCreateTime);
        return wrapper;
    }

    private void fillReviewDetails(List<CourseReview> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return;
        }

        // 获取所有会员ID
        List<Long> memberIds = reviews.stream()
                .map(CourseReview::getMemberId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // 获取所有课程ID
        List<Long> courseIds = reviews.stream()
                .map(CourseReview::getCourseId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // 获取所有教练ID
        List<Long> coachIds = reviews.stream()
                .map(CourseReview::getCoachId)
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
            reviews.forEach(review -> {
                if (review.getMemberId() != null) {
                    review.setMemberName(memberMap.get(review.getMemberId()));
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
            reviews.forEach(review -> {
                if (review.getCourseId() != null) {
                    review.setCourseName(courseMap.get(review.getCourseId()));
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
            reviews.forEach(review -> {
                if (review.getCoachId() != null) {
                    review.setCoachName(coachMap.get(review.getCoachId()));
                }
            });
        }
    }

    @Override
    public CourseReview getById(Long id) {
        CourseReview review = courseReviewMapper.selectById(id);
        if (review != null) {
            fillReviewDetails(Collections.singletonList(review));
        }
        return review;
    }

    @Override
    public boolean save(CourseReview courseReview) {
        if (courseReview.getStatus() == null) {
            courseReview.setStatus(1); // 默认显示
        }
        return courseReviewMapper.insert(courseReview) > 0;
    }

    @Override
    public boolean updateById(CourseReview courseReview) {
        return courseReviewMapper.updateById(courseReview) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return courseReviewMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reply(Long reviewId, String reply) {
        CourseReview review = courseReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        review.setReply(reply);
        review.setReplyTime(LocalDateTime.now());
        return courseReviewMapper.updateById(review) > 0;
    }

    @Override
    public List<CourseReview> getCourseReviews(Long courseId) {
        LambdaQueryWrapper<CourseReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReview::getCourseId, courseId)
               .eq(CourseReview::getStatus, 1)
               .orderByDesc(CourseReview::getCreateTime);
        List<CourseReview> list = courseReviewMapper.selectList(wrapper);
        fillReviewDetails(list);
        return list;
    }
}
