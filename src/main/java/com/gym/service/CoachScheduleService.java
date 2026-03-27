package com.gym.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.CoachSchedule;

import java.time.LocalDate;
import java.util.List;

public interface CoachScheduleService extends IService<CoachSchedule> {

    List<CoachSchedule> getScheduleList(LocalDate startDate, LocalDate endDate, Long coachId);

    boolean saveSchedule(CoachSchedule schedule);

    boolean updateSchedule(CoachSchedule schedule);
}
