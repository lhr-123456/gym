package com.gym.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.MemberInfo;
import com.gym.entity.MemberPointsRecord;
import com.gym.enums.PointsTaskType;
import com.gym.mapper.MemberPointsRecordMapper;
import com.gym.service.MemberInfoService;
import com.gym.service.MemberPointsRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberPointsRecordServiceImpl implements MemberPointsRecordService {

    private final MemberPointsRecordMapper recordMapper;
    private final MemberInfoService memberInfoService;

    public MemberPointsRecordServiceImpl(MemberPointsRecordMapper recordMapper,
                                         MemberInfoService memberInfoService) {
        this.recordMapper = recordMapper;
        this.memberInfoService = memberInfoService;
    }

    @Override
    public Page<MemberPointsRecord> getPageByMember(Long memberId, int pageNum, int pageSize, String taskType) {
        Page<MemberPointsRecord> page = new Page<>(pageNum, pageSize);
        return recordMapper.selectPageByMember(memberId, taskType, page);
    }

    @Override
    public List<MemberPointsRecord> getListByMember(Long memberId) {
        return recordMapper.selectByMemberId(memberId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordPoints(Long memberId, String taskType, String bizId, String bizType, String remark) {
        if (!canDoTask(memberId, taskType)) {
            return;
        }
        PointsTaskType task = PointsTaskType.fromKey(taskType);
        if (task == null) {
            return;
        }

        int points = task.getPoints();
        MemberPointsRecord record = new MemberPointsRecord();
        record.setMemberId(memberId);
        record.setTaskType(taskType);
        record.setTaskName(task.getName());
        record.setPoints(points);
        record.setBizId(bizId);
        record.setBizType(bizType);
        record.setRemark(remark);
        recordMapper.insert(record);

        MemberInfo member = memberInfoService.getById(memberId);
        if (member != null) {
            int current = member.getPoints() != null ? member.getPoints() : 0;
            member.setPoints(current + points);
            memberInfoService.updateById(member);
        }
    }

    @Override
    public int countTodayByMemberAndType(Long memberId, String taskType) {
        return recordMapper.countTodayByMemberAndType(memberId, taskType);
    }

    @Override
    public int countTotalByMemberAndType(Long memberId, String taskType) {
        return recordMapper.countTotalByMemberAndType(memberId, taskType);
    }

    @Override
    public boolean isTaskRepeatable(String taskType) {
        PointsTaskType task = PointsTaskType.fromKey(taskType);
        return task != null && task.isRepeatable();
    }

    @Override
    public boolean canDoTask(Long memberId, String taskType) {
        PointsTaskType task = PointsTaskType.fromKey(taskType);
        if (task == null) {
            return false;
        }
        if (task.isRepeatable()) {
            return countTodayByMemberAndType(memberId, taskType) == 0;
        } else {
            return countTotalByMemberAndType(memberId, taskType) == 0;
        }
    }

    @Override
    public int getTaskPoints(String taskType) {
        PointsTaskType task = PointsTaskType.fromKey(taskType);
        return task != null ? task.getPoints() : 0;
    }

    @Override
    public String getTaskName(String taskType) {
        PointsTaskType task = PointsTaskType.fromKey(taskType);
        return task != null ? task.getName() : taskType;
    }
}
