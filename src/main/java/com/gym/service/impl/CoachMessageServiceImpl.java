package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CoachMessage;
import com.gym.entity.MemberInfo;
import com.gym.mapper.CoachMessageMapper;
import com.gym.mapper.MemberInfoMapper;
import com.gym.service.CoachMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoachMessageServiceImpl implements CoachMessageService {

    private final CoachMessageMapper coachMessageMapper;
    private final MemberInfoMapper memberInfoMapper;

    public CoachMessageServiceImpl(CoachMessageMapper coachMessageMapper,
                                  MemberInfoMapper memberInfoMapper) {
        this.coachMessageMapper = coachMessageMapper;
        this.memberInfoMapper = memberInfoMapper;
    }

    @Override
    public Page<CoachMessage> getPage(int pageNum, int pageSize, Long coachId) {
        Page<CoachMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CoachMessage> wrapper = new LambdaQueryWrapper<>();
        if (coachId != null) {
            wrapper.eq(CoachMessage::getCoachId, coachId);
        }
        wrapper.orderByDesc(CoachMessage::getCreateTime);
        Page<CoachMessage> result = coachMessageMapper.selectPage(page, wrapper);
        fillMemberNames(result.getRecords());
        return result;
    }

    @Override
    public List<CoachMessage> getList(Long coachId) {
        LambdaQueryWrapper<CoachMessage> wrapper = new LambdaQueryWrapper<>();
        if (coachId != null) {
            wrapper.eq(CoachMessage::getCoachId, coachId);
        }
        wrapper.orderByDesc(CoachMessage::getCreateTime);
        List<CoachMessage> list = coachMessageMapper.selectList(wrapper);
        fillMemberNames(list);
        return list;
    }

    private void fillMemberNames(List<CoachMessage> messages) {
        if (messages == null || messages.isEmpty()) return;
        List<Long> memberIds = messages.stream()
                .map(CoachMessage::getMemberId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if (memberIds.isEmpty()) return;
        Map<Long, String> nameMap = memberInfoMapper.selectBatchIds(memberIds).stream()
                .collect(Collectors.toMap(MemberInfo::getMemberId, MemberInfo::getMemberName, (a, b) -> a));
        messages.forEach(m -> {
            if (m.getMemberId() != null) {
                m.setMemberName(nameMap.get(m.getMemberId()));
            }
        });
    }

    @Override
    public int getUnreadCount(Long coachId) {
        LambdaQueryWrapper<CoachMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachMessage::getCoachId, coachId)
               .eq(CoachMessage::getIsRead, 0);
        return coachMessageMapper.selectCount(wrapper).intValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long messageId) {
        CoachMessage msg = coachMessageMapper.selectById(messageId);
        if (msg != null && msg.getIsRead() == 0) {
            msg.setIsRead(1);
            coachMessageMapper.updateById(msg);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long coachId) {
        CoachMessage msg = new CoachMessage();
        msg.setIsRead(1);
        LambdaQueryWrapper<CoachMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachMessage::getCoachId, coachId)
               .eq(CoachMessage::getIsRead, 0);
        coachMessageMapper.update(msg, wrapper);
    }

    @Override
    public boolean save(CoachMessage message) {
        if (message.getIsRead() == null) {
            message.setIsRead(0);
        }
        return coachMessageMapper.insert(message) > 0;
    }
}
