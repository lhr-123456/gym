package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.MemberMessage;

import java.util.List;

public interface MemberMessageService {

    Page<MemberMessage> getPage(int pageNum, int pageSize, Long memberId, String type);

    List<MemberMessage> getList(Long memberId, String type);

    int getUnreadCount(Long memberId);

    void markRead(Long messageId);

    void markAllRead(Long memberId);

    boolean save(MemberMessage message);

    /**
     * 自动推送消息（供其他业务模块调用）
     */
    void pushMessage(Long memberId, String type, String title, String content, String refId, String refType);
}
