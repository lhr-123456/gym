package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.MemberPointsRecord;
import com.gym.enums.PointsTaskType;
import com.gym.service.MemberPointsRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member/points")
public class MemberPointsRecordController {

    private final MemberPointsRecordService pointsRecordService;

    public MemberPointsRecordController(MemberPointsRecordService pointsRecordService) {
        this.pointsRecordService = pointsRecordService;
    }

    /** 获取积分明细（分页） */
    @GetMapping("/record/page")
    public ApiResponse<Page<MemberPointsRecord>> getRecordPage(
            @RequestParam Long memberId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String taskType) {
        Page<MemberPointsRecord> page = pointsRecordService.getPageByMember(memberId, pageNum, pageSize, taskType);
        return ApiResponse.success(page);
    }

    /** 获取积分明细列表 */
    @GetMapping("/record/list")
    public ApiResponse<List<MemberPointsRecord>> getRecordList(@RequestParam Long memberId) {
        return ApiResponse.success(pointsRecordService.getListByMember(memberId));
    }

    /** 获取可用积分任务及当前完成状态 */
    @GetMapping("/task/list")
    public ApiResponse<List<Map<String, Object>>> getTaskList(@RequestParam Long memberId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (PointsTaskType task : PointsTaskType.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("taskType", task.getKey());
            item.put("taskName", task.getName());
            item.put("points", task.getPoints());
            item.put("repeatable", task.isRepeatable());
            item.put("todayCount", pointsRecordService.countTodayByMemberAndType(memberId, task.getKey()));
            item.put("totalCount", pointsRecordService.countTotalByMemberAndType(memberId, task.getKey()));
            item.put("canDo", pointsRecordService.canDoTask(memberId, task.getKey()));
            result.add(item);
        }
        return ApiResponse.success(result);
    }

    /** 手动触发积分记录（供签到/评价等业务接口内部调用，管理员可触发） */
    @PostMapping("/record/add")
    public ApiResponse<String> addRecord(@RequestParam Long memberId,
                                         @RequestParam String taskType,
                                         @RequestParam(required = false) String bizId,
                                         @RequestParam(required = false) String bizType,
                                         @RequestParam(required = false) String remark) {
        pointsRecordService.recordPoints(memberId, taskType, bizId, bizType, remark);
        return ApiResponse.success("积分已记录");
    }
}
