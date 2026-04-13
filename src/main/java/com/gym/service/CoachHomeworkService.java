package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CoachHomework;

import java.util.List;

public interface CoachHomeworkService {

    List<CoachHomework> listByCoach(Long coachId);

    List<CoachHomework> listByMember(Long memberId);

    List<CoachHomework> listPendingByMember(Long memberId);

    Page<CoachHomework> pageByCoach(Long coachId, int pageNum, int pageSize, Long memberId, Integer status);

    CoachHomework getById(Long id);

    boolean save(CoachHomework homework);

    boolean updateById(CoachHomework homework);

    boolean deleteById(Long id);

    boolean complete(Long id, Long memberId);

    boolean addCoachRemark(Long id, String remark);
}
