package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.CoachPerformance;

public interface CoachPerformanceService extends IService<CoachPerformance> {
    Page<CoachPerformance> getPage(Page<CoachPerformance> page, CoachPerformance coachPerformance);
}
