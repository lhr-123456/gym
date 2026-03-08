package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.MemberInfo;
import com.gym.mapper.MemberInfoMapper;
import com.gym.service.MemberInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberInfoServiceImpl implements MemberInfoService {

    private final MemberInfoMapper memberInfoMapper;

    public MemberInfoServiceImpl(MemberInfoMapper memberInfoMapper) {
        this.memberInfoMapper = memberInfoMapper;
    }

    @Override
    public Page<MemberInfo> getMemberPage(int pageNum, int pageSize, MemberInfo memberInfo) {
        Page<MemberInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MemberInfo> wrapper = buildQueryWrapper(memberInfo);
        return memberInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public MemberInfo getById(Long memberId) {
        return memberInfoMapper.selectById(memberId);
    }

    @Override
    public boolean save(MemberInfo memberInfo) {
        memberInfo.setRegTime(LocalDateTime.now());
        memberInfo.setAccountStatus(0);
        if (memberInfo.getPoints() == null) {
            memberInfo.setPoints(0);
        }
        if (memberInfo.getBalance() == null) {
            memberInfo.setBalance(0.0);
        }
        return memberInfoMapper.insert(memberInfo) > 0;
    }

    @Override
    public boolean updateById(MemberInfo memberInfo) {
        return memberInfoMapper.updateById(memberInfo) > 0;
    }

    @Override
    public boolean deleteById(Long memberId) {
        return memberInfoMapper.deleteById(memberId) > 0;
    }

    @Override
    public List<MemberInfo> list(MemberInfo memberInfo) {
        LambdaQueryWrapper<MemberInfo> wrapper = buildQueryWrapper(memberInfo);
        return memberInfoMapper.selectList(wrapper);
    }

    private LambdaQueryWrapper<MemberInfo> buildQueryWrapper(MemberInfo memberInfo) {
        LambdaQueryWrapper<MemberInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (memberInfo != null) {
            wrapper.eq(memberInfo.getMemberId() != null, MemberInfo::getMemberId, memberInfo.getMemberId())
                   .like(StringUtils.hasText(memberInfo.getMemberName()), MemberInfo::getMemberName, memberInfo.getMemberName())
                   .eq(StringUtils.hasText(memberInfo.getGender()), MemberInfo::getGender, memberInfo.getGender())
                   .eq(StringUtils.hasText(memberInfo.getPhoneNum()), MemberInfo::getPhoneNum, memberInfo.getPhoneNum())
                   .eq(StringUtils.hasText(memberInfo.getFitnessLevel()), MemberInfo::getFitnessLevel, memberInfo.getFitnessLevel())
                   .eq(memberInfo.getAccountStatus() != null, MemberInfo::getAccountStatus, memberInfo.getAccountStatus())
                   .eq(memberInfo.getMemberLevel() != null, MemberInfo::getMemberLevel, memberInfo.getMemberLevel());
        }
        
        wrapper.orderByDesc(MemberInfo::getRegTime);
        return wrapper;
    }
}
