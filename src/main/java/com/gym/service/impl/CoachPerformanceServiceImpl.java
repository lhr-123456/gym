package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CoachPerformance;
import com.gym.mapper.CoachPerformanceMapper;
import com.gym.service.CoachPerformanceService;
import org.springframework.stereotype.Service;

@Service
public class CoachPerformanceServiceImpl extends ServiceImpl<CoachPerformanceMapper, CoachPerformance> implements CoachPerformanceService {

    @Override
    public Page<CoachPerformance> getPage(Page<CoachPerformance> page, CoachPerformance coachPerformance) {
        LambdaQueryWrapper<CoachPerformance> wrapper = new LambdaQueryWrapper<>();
        if (coachPerformance != null) {
            if (coachPerformance.getCoachId() != null) {
                wrapper.eq(CoachPerformance::getCoachId, coachPerformance.getCoachId());
            }
            if (coachPerformance.getEvalMonth() != null && !coachPerformance.getEvalMonth().isEmpty()) {
                wrapper.eq(CoachPerformance::getEvalMonth, coachPerformance.getEvalMonth());
            }
        }
        wrapper.orderByDesc(CoachPerformance::getCreateTime);
        return page(page, wrapper);
    }
}
