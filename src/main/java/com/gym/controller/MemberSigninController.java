package com.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.MemberSignin;
import com.gym.mapper.MemberSigninMapper;
import com.gym.service.MemberPointsRecordService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/member/signin")
public class MemberSigninController {

    private final MemberSigninMapper memberSigninMapper;
    private final MemberPointsRecordService pointsRecordService;

    public MemberSigninController(MemberSigninMapper memberSigninMapper,
                                 MemberPointsRecordService pointsRecordService) {
        this.memberSigninMapper = memberSigninMapper;
        this.pointsRecordService = pointsRecordService;
    }

    /**
     * 获取所有签到记录
     */
    @GetMapping("/list")
    public ApiResponse<List<MemberSignin>> list() {
        LambdaQueryWrapper<MemberSignin> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MemberSignin::getSigninTime);
        List<MemberSignin> list = memberSigninMapper.selectList(wrapper);
        return ApiResponse.success(list);
    }

    /**
     * 分页获取签到记录
     */
    @GetMapping("/page")
    public ApiResponse<Page<MemberSignin>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String signinDate) {
        Page<MemberSignin> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MemberSignin> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) {
            wrapper.eq(MemberSignin::getMemberId, memberId);
        }
        if (signinDate != null && !signinDate.isEmpty()) {
            wrapper.eq(MemberSignin::getSigninDate, LocalDate.parse(signinDate));
        }
        wrapper.orderByDesc(MemberSignin::getSigninTime);
        Page<MemberSignin> result = memberSigninMapper.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    /**
     * 会员签到
     * 校验今日是否已签到，计算连续签到天数，根据连续天数发放阶梯积分奖励（7天20分、30天30分，其余10分）
     */
    @PostMapping("/sign")
    public ApiResponse<String> sign(@RequestBody MemberSignin signin) {
        // 检查今天是否已签到
        LambdaQueryWrapper<MemberSignin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberSignin::getMemberId, signin.getMemberId());
        wrapper.eq(MemberSignin::getSigninDate, LocalDate.now());
        MemberSignin todaySignin = memberSigninMapper.selectOne(wrapper);
        
        if (todaySignin != null) {
            return ApiResponse.error("今日已签到");
        }

        // 计算连续签到天数
        LocalDate yesterday = LocalDate.now().minusDays(1);
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberSignin::getMemberId, signin.getMemberId());
        wrapper.eq(MemberSignin::getSigninDate, yesterday);
        MemberSignin yesterdaySignin = memberSigninMapper.selectOne(wrapper);
        
        int consecutiveDays = 1;
        if (yesterdaySignin != null) {
            consecutiveDays = yesterdaySignin.getConsecutiveDays() + 1;
        }

        // 设置签到信息
        signin.setSigninDate(LocalDate.now());
        signin.setSigninTime(LocalDateTime.now());
        signin.setSigninType("日常签到");
        signin.setConsecutiveDays(consecutiveDays);
        
        // 签到奖励积分（连续签到越多积分越多）
        int basePoints = 10;
        if (consecutiveDays >= 7) {
            basePoints = 20;
        } else if (consecutiveDays >= 30) {
            basePoints = 30;
        }
        signin.setPointsEarned(basePoints);

        memberSigninMapper.insert(signin);

        // 记录积分明细
        pointsRecordService.recordPoints(
            signin.getMemberId(),
            "signin",
            String.valueOf(signin.getSigninId()),
            "member_signin",
            "连续签到" + consecutiveDays + "天"
        );

        return ApiResponse.success("签到成功，获得" + basePoints + "积分，连续签到" + consecutiveDays + "天");
    }

    /**
     * 检查会员今日是否已签到
     * 返回布尔值，true表示已签到
     */
    @GetMapping("/today/{memberId}")
    public ApiResponse<Boolean> checkTodaySignin(@PathVariable Long memberId) {
        LambdaQueryWrapper<MemberSignin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberSignin::getMemberId, memberId);
        wrapper.eq(MemberSignin::getSigninDate, LocalDate.now());
        MemberSignin signin = memberSigninMapper.selectOne(wrapper);
        return ApiResponse.success(signin != null);
    }

    /**
     * 获取会员签到统计
     * 统计总签到次数、总获得积分、最长连续签到天数、本月签到次数
     */
    @GetMapping("/statistics/{memberId}")
    public ApiResponse<Object> getStatistics(@PathVariable Long memberId) {
        // 总签到次数
        LambdaQueryWrapper<MemberSignin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberSignin::getMemberId, memberId);
        Long totalCount = memberSigninMapper.selectCount(wrapper);

        // 总获得积分
        List<MemberSignin> signins = memberSigninMapper.selectList(wrapper);
        int totalPoints = signins.stream().mapToInt(s -> s.getPointsEarned() != null ? s.getPointsEarned() : 0).sum();

        // 最长连续签到
        int maxConsecutive = 0;
        int currentConsecutive = 0;
        LocalDate lastDate = null;
        for (MemberSignin s : signins) {
            if (lastDate == null) {
                currentConsecutive = 1;
            } else if (lastDate.minusDays(1).equals(s.getSigninDate())) {
                currentConsecutive++;
            } else {
                maxConsecutive = Math.max(maxConsecutive, currentConsecutive);
                currentConsecutive = 1;
            }
            lastDate = s.getSigninDate();
        }
        maxConsecutive = Math.max(maxConsecutive, currentConsecutive);

        // 本月签到次数
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberSignin::getMemberId, memberId);
        wrapper.ge(MemberSignin::getSigninDate, firstDayOfMonth);
        Long monthCount = memberSigninMapper.selectCount(wrapper);

        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalCount", totalCount);
        statistics.put("totalPoints", totalPoints);
        statistics.put("maxConsecutive", maxConsecutive);
        statistics.put("monthCount", monthCount);

        return ApiResponse.success(statistics);
    }

    /**
     * 删除签到记录
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        memberSigninMapper.deleteById(id);
        return ApiResponse.success("删除成功");
    }
}
