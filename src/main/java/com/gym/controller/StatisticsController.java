package com.gym.controller;

import com.gym.dto.ApiResponse;
import com.gym.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final MemberInfoMapper memberInfoMapper;
    private final CoachInfoMapper coachInfoMapper;
    private final CourseInfoMapper courseInfoMapper;
    private final EquipmentInfoMapper equipmentInfoMapper;

    public StatisticsController(MemberInfoMapper memberInfoMapper,
                              CoachInfoMapper coachInfoMapper,
                              CourseInfoMapper courseInfoMapper,
                              EquipmentInfoMapper equipmentInfoMapper) {
        this.memberInfoMapper = memberInfoMapper;
        this.coachInfoMapper = coachInfoMapper;
        this.courseInfoMapper = courseInfoMapper;
        this.equipmentInfoMapper = equipmentInfoMapper;
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboardStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 会员总数
            Long memberCount = memberInfoMapper.selectCount(null);
            statistics.put("memberCount", memberCount);

            // 教练总数
            Long coachCount = coachInfoMapper.selectCount(null);
            statistics.put("coachCount", coachCount);

            // 课程总数
            Long courseCount = courseInfoMapper.selectCount(null);
            statistics.put("courseCount", courseCount);

            // 器材总数
            Long equipmentCount = equipmentInfoMapper.selectCount(null);
            statistics.put("equipmentCount", equipmentCount);

            // 今日新增会员数
            // 可以根据需要添加更复杂的统计逻辑

            return ApiResponse.success(statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败：" + e.getMessage());
        }
    }
}
