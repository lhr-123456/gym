package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.CoachMessage;

import java.util.List;

public interface CoachMessageService {

    Page<CoachMessage> getPage(int pageNum, int pageSize, Long coachId);

    List<CoachMessage> getList(Long coachId);

    int getUnreadCount(Long coachId);

    void markRead(Long messageId);

    void markAllRead(Long coachId);

    boolean save(CoachMessage message);
}
