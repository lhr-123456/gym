package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CoachInfo;
import com.gym.mapper.CoachInfoMapper;
import com.gym.service.CoachInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class CoachInfoServiceImpl implements CoachInfoService {

    private final CoachInfoMapper coachInfoMapper;

    public CoachInfoServiceImpl(CoachInfoMapper coachInfoMapper) {
        this.coachInfoMapper = coachInfoMapper;
    }

    @Override
    public Page<CoachInfo> getCoachPage(int pageNum, int pageSize, CoachInfo coachInfo) {
        Page<CoachInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CoachInfo> wrapper = buildQueryWrapper(coachInfo);
        return coachInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public CoachInfo getById(Long coachId) {
        return coachInfoMapper.selectById(coachId);
    }

    @Override
    public boolean save(CoachInfo coachInfo) {
        if (coachInfo.getStatus() == null) {
            coachInfo.setStatus(1);
        }
        if (coachInfo.getHireDate() == null) {
            coachInfo.setHireDate(LocalDate.now());
        }
        return coachInfoMapper.insert(coachInfo) > 0;
    }

    @Override
    public boolean updateById(CoachInfo coachInfo) {
        return coachInfoMapper.updateById(coachInfo) > 0;
    }

    @Override
    public boolean deleteById(Long coachId) {
        return coachInfoMapper.deleteById(coachId) > 0;
    }

    @Override
    public List<CoachInfo> list(CoachInfo coachInfo) {
        LambdaQueryWrapper<CoachInfo> wrapper = buildQueryWrapper(coachInfo);
        return coachInfoMapper.selectList(wrapper);
    }

    private LambdaQueryWrapper<CoachInfo> buildQueryWrapper(CoachInfo coachInfo) {
        LambdaQueryWrapper<CoachInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (coachInfo != null) {
            wrapper.eq(coachInfo.getCoachId() != null, CoachInfo::getCoachId, coachInfo.getCoachId())
                   .like(StringUtils.hasText(coachInfo.getCoachName()), CoachInfo::getCoachName, coachInfo.getCoachName())
                   .eq(StringUtils.hasText(coachInfo.getGender()), CoachInfo::getGender, coachInfo.getGender())
                   .eq(StringUtils.hasText(coachInfo.getSpecialty()), CoachInfo::getSpecialty, coachInfo.getSpecialty())
                   .eq(coachInfo.getStatus() != null, CoachInfo::getStatus, coachInfo.getStatus());
        }
        
        wrapper.orderByDesc(CoachInfo::getHireDate);
        return wrapper;
    }
}
