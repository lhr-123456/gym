package com.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.MemberLevel;
import com.gym.mapper.MemberLevelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member/level")
public class MemberLevelController {

    private final MemberLevelMapper memberLevelMapper;

    public MemberLevelController(MemberLevelMapper memberLevelMapper) {
        this.memberLevelMapper = memberLevelMapper;
    }

    /**
     * 获取所有会员等级列表
     */
    @GetMapping("/list")
    public ApiResponse<List<MemberLevel>> list() {
        LambdaQueryWrapper<MemberLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MemberLevel::getLevelOrder);
        List<MemberLevel> list = memberLevelMapper.selectList(wrapper);
        return ApiResponse.success(list);
    }

    /**
     * 分页获取会员等级列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<MemberLevel>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<MemberLevel> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MemberLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MemberLevel::getLevelOrder);
        Page<MemberLevel> result = memberLevelMapper.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    /**
     * 获取会员等级详情
     */
    @GetMapping("/{id}")
    public ApiResponse<MemberLevel> getById(@PathVariable Long id) {
        MemberLevel level = memberLevelMapper.selectById(id);
        return ApiResponse.success(level);
    }

    /**
     * 新增会员等级
     */
    @PostMapping
    public ApiResponse<String> add(@RequestBody MemberLevel memberLevel) {
        memberLevelMapper.insert(memberLevel);
        return ApiResponse.success("添加成功");
    }

    /**
     * 修改会员等级
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody MemberLevel memberLevel) {
        memberLevelMapper.updateById(memberLevel);
        return ApiResponse.success("修改成功");
    }

    /**
     * 删除会员等级
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        memberLevelMapper.deleteById(id);
        return ApiResponse.success("删除成功");
    }

    /**
     * 初始化默认会员等级
     */
    @PostMapping("/init")
    public ApiResponse<String> initDefaultLevels() {
        // 检查是否已有数据
        Long count = memberLevelMapper.selectCount(null);
        if (count > 0) {
            return ApiResponse.success("会员等级已存在，无需初始化");
        }

        // 插入默认等级
        MemberLevel bronze = new MemberLevel();
        bronze.setLevelName("青铜会员");
        bronze.setLevelCode("BRONZE");
        bronze.setLevelOrder(1);
        bronze.setDiscountRate(new java.math.BigDecimal("0.98"));
        bronze.setPointsRate(new java.math.BigDecimal("1.0"));
        bronze.setMinPoints(0);
        bronze.setMaxPoints(1000);
        bronze.setCardFee(99.0);
        bronze.setDescription("青铜会员，享98折优惠");
        bronze.setIcon("el-icon-medal");
        bronze.setIconColor("#CD7F32");
        memberLevelMapper.insert(bronze);

        MemberLevel silver = new MemberLevel();
        silver.setLevelName("白银会员");
        silver.setLevelCode("SILVER");
        silver.setLevelOrder(2);
        silver.setDiscountRate(new java.math.BigDecimal("0.95"));
        silver.setPointsRate(new java.math.BigDecimal("1.2"));
        silver.setMinPoints(1000);
        silver.setMaxPoints(5000);
        silver.setCardFee(199.0);
        silver.setDescription("白银会员，享95折优惠，1.2倍积分");
        silver.setIcon("el-icon-medal");
        silver.setIconColor("#C0C0C0");
        memberLevelMapper.insert(silver);

        MemberLevel gold = new MemberLevel();
        gold.setLevelName("黄金会员");
        gold.setLevelCode("GOLD");
        gold.setLevelOrder(3);
        gold.setDiscountRate(new java.math.BigDecimal("0.90"));
        gold.setPointsRate(new java.math.BigDecimal("1.5"));
        gold.setMinPoints(5000);
        gold.setMaxPoints(20000);
        gold.setCardFee(399.0);
        gold.setDescription("黄金会员，享9折优惠，1.5倍积分");
        gold.setIcon("el-icon-medal");
        gold.setIconColor("#FFD700");
        memberLevelMapper.insert(gold);

        MemberLevel diamond = new MemberLevel();
        diamond.setLevelName("钻石会员");
        diamond.setLevelCode("DIAMOND");
        diamond.setLevelOrder(4);
        diamond.setDiscountRate(new java.math.BigDecimal("0.80"));
        diamond.setPointsRate(new java.math.BigDecimal("2.0"));
        diamond.setMinPoints(20000);
        diamond.setMaxPoints(999999999);
        diamond.setCardFee(799.0);
        diamond.setDescription("钻石会员，享8折优惠，2倍积分");
        diamond.setIcon("el-icon-medal");
        diamond.setIconColor("#B9F2FF");
        memberLevelMapper.insert(diamond);

        return ApiResponse.success("初始化成功");
    }
}
