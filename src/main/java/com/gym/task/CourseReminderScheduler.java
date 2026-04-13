package com.gym.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gym.entity.CourseReminder;
import com.gym.mapper.CourseReminderMapper;
import com.gym.service.MemberMessageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CourseReminderScheduler {

    private final CourseReminderMapper reminderMapper;
    private final MemberMessageService memberMessageService;

    public CourseReminderScheduler(CourseReminderMapper reminderMapper,
                                   MemberMessageService memberMessageService) {
        this.reminderMapper = reminderMapper;
        this.memberMessageService = memberMessageService;
    }

    /** 每10分钟执行一次：查找开课前1小时内且未发送提醒的记录 */
    @Scheduled(fixedRate = 600000)
    public void sendClassReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        LambdaQueryWrapper<CourseReminder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReminder::getRemindStatus, 0)
               .le(CourseReminder::getClassTime, oneHourLater)
               .ge(CourseReminder::getClassTime, now)
               .apply("deleted = 0");
        List<CourseReminder> reminders = reminderMapper.selectList(wrapper);

        for (CourseReminder reminder : reminders) {
            String content = String.format(
                "您报名的「%s」将于 %s 开课，请提前到达%s做好准备！",
                reminder.getCourseName(),
                reminder.getClassTime() != null ? reminder.getClassTime().toString().replace("T", " ") : "即将",
                reminder.getLocation() != null ? "（地点：" + reminder.getLocation() + "）" : ""
            );
            memberMessageService.pushMessage(
                reminder.getMemberId(),
                "reminder",
                "课程提醒",
                content,
                String.valueOf(reminder.getBookingId()),
                "course_reminder"
            );

            reminder.setRemindStatus(1);
            reminder.setRemindTime(LocalDateTime.now());
            reminderMapper.updateById(reminder);
        }
    }
}
