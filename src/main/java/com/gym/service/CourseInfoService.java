package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CourseBooking;
import com.gym.entity.CourseInfo;

import java.util.List;

public interface CourseInfoService {

    Page<CourseInfo> getCoursePage(int pageNum, int pageSize, CourseInfo courseInfo);

    Page<CourseInfo> getAvailableCourses(int pageNum, int pageSize, CourseInfo courseInfo);

    List<CourseBooking> getCourseBookings(Long courseId);

    CourseInfo getById(Long courseId);

    boolean save(CourseInfo courseInfo);

    boolean updateById(CourseInfo courseInfo);

    boolean deleteById(Long courseId);

    List<CourseInfo> list(CourseInfo courseInfo);

    boolean bookCourse(Long courseId, Long memberId, Long coachId);

    boolean cancelBooking(Long bookingId);
}
