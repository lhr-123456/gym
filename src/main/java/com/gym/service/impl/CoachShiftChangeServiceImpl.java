package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CoachShiftChange;
import com.gym.mapper.CoachShiftChangeMapper;
import com.gym.service.CoachShiftChangeService;
import org.springframework.stereotype.Service;

@Service
public class CoachShiftChangeServiceImpl extends ServiceImpl<CoachShiftChangeMapper, CoachShiftChange> implements CoachShiftChangeService {
}
