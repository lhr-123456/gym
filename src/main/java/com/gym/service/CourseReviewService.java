package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CourseReview;

import java.util.List;

public interface CourseReviewService {

    Page<CourseReview> getPage(int pageNum, int pageSize, CourseReview courseReview);

    List<CourseReview> getList(CourseReview courseReview);

    CourseReview getById(Long id);

    boolean save(CourseReview courseReview);

    boolean updateById(CourseReview courseReview);

    boolean deleteById(Long id);

    boolean reply(Long reviewId, String reply);

    List<CourseReview> getCourseReviews(Long courseId);
}
