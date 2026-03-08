package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CoachInfo;

import java.util.List;

public interface CoachInfoService {

    Page<CoachInfo> getCoachPage(int pageNum, int pageSize, CoachInfo coachInfo);

    CoachInfo getById(Long coachId);

    boolean save(CoachInfo coachInfo);

    boolean updateById(CoachInfo coachInfo);

    boolean deleteById(Long coachId);

    List<CoachInfo> list(CoachInfo coachInfo);
}
