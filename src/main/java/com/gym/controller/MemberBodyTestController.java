package com.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.MemberBodyTest;
import com.gym.mapper.MemberBodyTestMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member/bodyTest")
public class MemberBodyTestController {

    private final MemberBodyTestMapper memberBodyTestMapper;

    public MemberBodyTestController(MemberBodyTestMapper memberBodyTestMapper) {
        this.memberBodyTestMapper = memberBodyTestMapper;
    }

    /**
     * 获取所有体测记录
     */
    @GetMapping("/list")
    public ApiResponse<List<MemberBodyTest>> list() {
        LambdaQueryWrapper<MemberBodyTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MemberBodyTest::getTestDate);
        List<MemberBodyTest> list = memberBodyTestMapper.selectList(wrapper);
        return ApiResponse.success(list);
    }

    /**
     * 分页获取体测记录
     */
    @GetMapping("/page")
    public ApiResponse<Page<MemberBodyTest>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long memberId) {
        Page<MemberBodyTest> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MemberBodyTest> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) {
            wrapper.eq(MemberBodyTest::getMemberId, memberId);
        }
        wrapper.orderByDesc(MemberBodyTest::getTestDate);
        Page<MemberBodyTest> result = memberBodyTestMapper.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    /**
     * 获取体测记录详情
     */
    @GetMapping("/{id}")
    public ApiResponse<MemberBodyTest> getById(@PathVariable Long id) {
        MemberBodyTest test = memberBodyTestMapper.selectById(id);
        return ApiResponse.success(test);
    }

    /**
     * 根据会员ID获取体测记录
     */
    @GetMapping("/member/{memberId}")
    public ApiResponse<List<MemberBodyTest>> getByMemberId(@PathVariable Long memberId) {
        LambdaQueryWrapper<MemberBodyTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberBodyTest::getMemberId, memberId);
        wrapper.orderByDesc(MemberBodyTest::getTestDate);
        List<MemberBodyTest> list = memberBodyTestMapper.selectList(wrapper);
        return ApiResponse.success(list);
    }

    /**
     * 获取会员最新体测记录
     */
    @GetMapping("/latest/{memberId}")
    public ApiResponse<MemberBodyTest> getLatestByMemberId(@PathVariable Long memberId) {
        LambdaQueryWrapper<MemberBodyTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberBodyTest::getMemberId, memberId);
        wrapper.orderByDesc(MemberBodyTest::getTestDate);
        wrapper.last("LIMIT 1");
        MemberBodyTest test = memberBodyTestMapper.selectOne(wrapper);
        return ApiResponse.success(test);
    }

    /**
     * 新增体测记录
     */
    @PostMapping
    public ApiResponse<String> add(@RequestBody MemberBodyTest memberBodyTest) {
        // 计算BMI
        if (memberBodyTest.getHeight() != null && memberBodyTest.getWeight() != null) {
            double heightM = memberBodyTest.getHeight() / 100.0;
            double bmi = memberBodyTest.getWeight() / (heightM * heightM);
            memberBodyTest.setBmi(Math.round(bmi * 10) / 10.0);
        }

        // 计算健康评分（简单示例）
        if (memberBodyTest.getBmi() != null) {
            int score = 100;
            if (memberBodyTest.getBmi() < 18.5) {
                score -= 10;
            } else if (memberBodyTest.getBmi() > 24) {
                score -= 10;
            }
            if (memberBodyTest.getBodyFatRate() != null) {
                if (memberBodyTest.getBodyFatRate() > 25) {
                    score -= 10;
                }
            }
            memberBodyTest.setHealthScore(score);
        }

        memberBodyTestMapper.insert(memberBodyTest);
        return ApiResponse.success("添加成功");
    }

    /**
     * 修改体测记录
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody MemberBodyTest memberBodyTest) {
        // 重新计算BMI
        if (memberBodyTest.getHeight() != null && memberBodyTest.getWeight() != null) {
            double heightM = memberBodyTest.getHeight() / 100.0;
            double bmi = memberBodyTest.getWeight() / (heightM * heightM);
            memberBodyTest.setBmi(Math.round(bmi * 10) / 10.0);
        }

        memberBodyTestMapper.updateById(memberBodyTest);
        return ApiResponse.success("修改成功");
    }

    /**
     * 删除体测记录
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        memberBodyTestMapper.deleteById(id);
        return ApiResponse.success("删除成功");
    }
}
