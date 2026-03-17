package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CoachSchedule;
import com.gym.mapper.CoachScheduleMapper;
import com.gym.service.CoachScheduleService;
import org.springframework.stereotype.Service;

@Service
public class CoachScheduleServiceImpl extends ServiceImpl<CoachScheduleMapper, CoachSchedule> implements CoachScheduleService {
}
