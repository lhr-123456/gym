package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.MemberMessage;
import com.gym.mapper.MemberMessageMapper;
import com.gym.service.MemberMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberMessageServiceImpl implements MemberMessageService {

    private final MemberMessageMapper memberMessageMapper;

    public MemberMessageServiceImpl(MemberMessageMapper memberMessageMapper) {
        this.memberMessageMapper = memberMessageMapper;
    }

    @Override
    public Page<MemberMessage> getPage(int pageNum, int pageSize, Long memberId, String type) {
        Page<MemberMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MemberMessage> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) {
            wrapper.eq(MemberMessage::getMemberId, memberId);
        }
        if (type != null && !"all".equals(type)) {
            wrapper.eq(MemberMessage::getType, type);
        }
        wrapper.orderByDesc(MemberMessage::getCreateTime);
        return memberMessageMapper.selectPage(page, wrapper);
    }

    @Override
    public List<MemberMessage> getList(Long memberId, String type) {
        LambdaQueryWrapper<MemberMessage> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) {
            wrapper.eq(MemberMessage::getMemberId, memberId);
        }
        if (type != null && !"all".equals(type)) {
            wrapper.eq(MemberMessage::getType, type);
        }
        wrapper.orderByDesc(MemberMessage::getCreateTime);
        return memberMessageMapper.selectList(wrapper);
    }

    @Override
    public int getUnreadCount(Long memberId) {
        LambdaQueryWrapper<MemberMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberMessage::getMemberId, memberId)
               .eq(MemberMessage::getIsRead, 0);
        return memberMessageMapper.selectCount(wrapper).intValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long messageId) {
        MemberMessage msg = memberMessageMapper.selectById(messageId);
        if (msg != null && msg.getIsRead() == 0) {
            msg.setIsRead(1);
            memberMessageMapper.updateById(msg);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long memberId) {
        MemberMessage msg = new MemberMessage();
        msg.setIsRead(1);
        LambdaQueryWrapper<MemberMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberMessage::getMemberId, memberId)
               .eq(MemberMessage::getIsRead, 0);
        memberMessageMapper.update(msg, wrapper);
    }

    @Override
    public boolean save(MemberMessage message) {
        if (message.getIsRead() == null) {
            message.setIsRead(0);
        }
        return memberMessageMapper.insert(message) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushMessage(Long memberId, String type, String title, String content, String refId, String refType) {
        MemberMessage msg = new MemberMessage();
        msg.setMemberId(memberId);
        msg.setType(type);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setRefId(refId);
        msg.setRefType(refType);
        msg.setIsRead(0);
        memberMessageMapper.insert(msg);
    }
}
