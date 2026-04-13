package com.gym.service;

import com.gym.entity.CourseReminder;

import java.util.List;

public interface CourseReminderService {

    void createReminder(Long memberId, Long bookingId, Long courseId,
                        String courseName, String coachName,
                        java.time.LocalDateTime classTime, String location);

    List<CourseReminder> getByMemberId(Long memberId);

    List<CourseReminder> getUpcomingByMemberId(Long memberId);

    void markReminderSent(Long id);
}
