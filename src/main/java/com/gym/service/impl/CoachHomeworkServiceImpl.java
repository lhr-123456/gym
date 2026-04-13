package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CoachHomework;
import com.gym.mapper.CoachHomeworkMapper;
import com.gym.service.CoachHomeworkService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoachHomeworkServiceImpl implements CoachHomeworkService {

    private final CoachHomeworkMapper homeworkMapper;

    public CoachHomeworkServiceImpl(CoachHomeworkMapper homeworkMapper) {
        this.homeworkMapper = homeworkMapper;
    }

    @Override
    public List<CoachHomework> listByCoach(Long coachId) {
        return homeworkMapper.selectByCoachId(coachId);
    }

    @Override
    public List<CoachHomework> listByMember(Long memberId) {
        return homeworkMapper.selectByMemberId(memberId);
    }

    @Override
    public List<CoachHomework> listPendingByMember(Long memberId) {
        return homeworkMapper.selectPendingByMemberId(memberId);
    }

    @Override
    public Page<CoachHomework> pageByCoach(Long coachId, int pageNum, int pageSize, Long memberId, Integer status) {
        Page<CoachHomework> page = new Page<>(pageNum, pageSize);
        return homeworkMapper.selectPageByCoach(coachId, memberId, status, page);
    }

    @Override
    public CoachHomework getById(Long id) {
        return homeworkMapper.selectById(id);
    }

    @Override
    public boolean save(CoachHomework homework) {
        if (homework.getStatus() == null) {
            homework.setStatus(0);
        }
        return homeworkMapper.insert(homework) > 0;
    }

    @Override
    public boolean updateById(CoachHomework homework) {
        return homeworkMapper.updateById(homework) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return homeworkMapper.deleteById(id) > 0;
    }

    @Override
    public boolean complete(Long id, Long memberId) {
        CoachHomework homework = homeworkMapper.selectById(id);
        if (homework == null || !homework.getMemberId().equals(memberId)) {
            return false;
        }
        homework.setStatus(1);
        homework.setCompleteTime(LocalDateTime.now());
        return homeworkMapper.updateById(homework) > 0;
    }

    @Override
    public boolean addCoachRemark(Long id, String remark) {
        CoachHomework homework = homeworkMapper.selectById(id);
        if (homework == null) {
            return false;
        }
        homework.setCoachRemark(remark);
        return homeworkMapper.updateById(homework) > 0;
    }
}
