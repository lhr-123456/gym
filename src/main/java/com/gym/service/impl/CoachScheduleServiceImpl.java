package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CoachSchedule;
import com.gym.mapper.CoachScheduleMapper;
import com.gym.service.CoachScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CoachScheduleServiceImpl extends ServiceImpl<CoachScheduleMapper, CoachSchedule>
        implements CoachScheduleService {

    @Override
    public List<CoachSchedule> getScheduleList(LocalDate startDate, LocalDate endDate, Long coachId) {
        LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachSchedule::getDeleted, 0);
        if (startDate != null) {
            wrapper.ge(CoachSchedule::getScheduleDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(CoachSchedule::getScheduleDate, endDate);
        }
        if (coachId != null) {
            wrapper.eq(CoachSchedule::getCoachId, coachId);
        }
        wrapper.orderByAsc(CoachSchedule::getScheduleDate)
               .orderByAsc(CoachSchedule::getStartTime);
        return list(wrapper);
    }

    @Override
    public boolean saveSchedule(CoachSchedule schedule) {
        if (schedule.getStatus() == null) {
            schedule.setStatus(1); // 默认待上课
        }
        if (schedule.getCurrentCapacity() == null) {
            schedule.setCurrentCapacity(0);
        }
        return save(schedule);
    }

    @Override
    public boolean updateSchedule(CoachSchedule schedule) {
        return updateById(schedule);
    }
}
