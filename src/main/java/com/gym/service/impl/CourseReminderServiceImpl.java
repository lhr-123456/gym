package com.gym.service.impl;

import com.gym.entity.CourseReminder;
import com.gym.mapper.CourseReminderMapper;
import com.gym.service.CourseReminderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseReminderServiceImpl implements CourseReminderService {

    private final CourseReminderMapper reminderMapper;

    public CourseReminderServiceImpl(CourseReminderMapper reminderMapper) {
        this.reminderMapper = reminderMapper;
    }

    @Override
    public void createReminder(Long memberId, Long bookingId, Long courseId,
                               String courseName, String coachName,
                               java.time.LocalDateTime classTime, String location) {
        CourseReminder reminder = new CourseReminder();
        reminder.setMemberId(memberId);
        reminder.setBookingId(bookingId);
        reminder.setCourseId(courseId);
        reminder.setCourseName(courseName);
        reminder.setCoachName(coachName);
        reminder.setClassTime(classTime);
        reminder.setLocation(location);
        reminder.setRemindStatus(0);
        reminderMapper.insert(reminder);
    }

    @Override
    public List<CourseReminder> getByMemberId(Long memberId) {
        return reminderMapper.selectByMemberId(memberId);
    }

    @Override
    public List<CourseReminder> getUpcomingByMemberId(Long memberId) {
        return reminderMapper.selectUpcomingByMemberId(memberId);
    }

    @Override
    public void markReminderSent(Long id) {
        CourseReminder reminder = reminderMapper.selectById(id);
        if (reminder != null) {
            reminder.setRemindStatus(1);
            reminder.setRemindTime(LocalDateTime.now());
            reminderMapper.updateById(reminder);
        }
    }
}
