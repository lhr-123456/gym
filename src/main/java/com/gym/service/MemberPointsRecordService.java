package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.MemberPointsRecord;

import java.util.List;

public interface MemberPointsRecordService {

    Page<MemberPointsRecord> getPageByMember(Long memberId, int pageNum, int pageSize, String taskType);

    List<MemberPointsRecord> getListByMember(Long memberId);

    void recordPoints(Long memberId, String taskType, String bizId, String bizType, String remark);

    int countTodayByMemberAndType(Long memberId, String taskType);

    int countTotalByMemberAndType(Long memberId, String taskType);

    boolean isTaskRepeatable(String taskType);

    boolean canDoTask(Long memberId, String taskType);

    int getTaskPoints(String taskType);

    String getTaskName(String taskType);
}
