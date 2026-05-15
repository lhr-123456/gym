package com.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.MemberCard;
import com.gym.entity.MemberConsumption;
import com.gym.mapper.MemberCardMapper;
import com.gym.mapper.MemberConsumptionMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/member/consumption")
public class MemberConsumptionController {

    private final MemberConsumptionMapper memberConsumptionMapper;
    private final MemberCardMapper memberCardMapper;

    public MemberConsumptionController(MemberConsumptionMapper memberConsumptionMapper,
                                        MemberCardMapper memberCardMapper) {
        this.memberConsumptionMapper = memberConsumptionMapper;
        this.memberCardMapper = memberCardMapper;
    }

    /**
     * 获取所有消费记录
     */
    @GetMapping("/list")
    public ApiResponse<List<MemberConsumption>> list() {
        LambdaQueryWrapper<MemberConsumption> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MemberConsumption::getConsumptionTime);
        List<MemberConsumption> list = memberConsumptionMapper.selectList(wrapper);
        return ApiResponse.success(list);
    }

    /**
     * 分页获取消费记录
     */
    @GetMapping("/page")
    public ApiResponse<Page<MemberConsumption>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String consumptionType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<MemberConsumption> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MemberConsumption> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) {
            wrapper.eq(MemberConsumption::getMemberId, memberId);
        }
        if (consumptionType != null && !consumptionType.isEmpty()) {
            wrapper.eq(MemberConsumption::getConsumptionType, consumptionType);
        }
        wrapper.orderByDesc(MemberConsumption::getConsumptionTime);
        Page<MemberConsumption> result = memberConsumptionMapper.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    /**
     * 获取消费记录详情
     */
    @GetMapping("/{id}")
    public ApiResponse<MemberConsumption> getById(@PathVariable Long id) {
        MemberConsumption consumption = memberConsumptionMapper.selectById(id);
        return ApiResponse.success(consumption);
    }

    /**
     * 根据会员ID获取消费记录
     */
    @GetMapping("/member/{memberId}")
    public ApiResponse<List<MemberConsumption>> getByMemberId(@PathVariable Long memberId) {
        LambdaQueryWrapper<MemberConsumption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberConsumption::getMemberId, memberId);
        wrapper.orderByDesc(MemberConsumption::getConsumptionTime);
        List<MemberConsumption> list = memberConsumptionMapper.selectList(wrapper);
        return ApiResponse.success(list);
    }

    /**
     * 新增消费记录
     */
    @PostMapping
    public ApiResponse<String> add(@RequestBody MemberConsumption consumption) {
        // 生成订单号
        String orderNo = "O" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        consumption.setOrderNo(orderNo);

        // 设置消费时间
        if (consumption.getConsumptionTime() == null) {
            consumption.setConsumptionTime(LocalDateTime.now());
        }

        // 设置状态
        if (consumption.getStatus() == null) {
            consumption.setStatus(1); // 已完成
        }

        memberConsumptionMapper.insert(consumption);
        return ApiResponse.success("添加成功");
    }

    /**
     * 修改消费记录
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody MemberConsumption consumption) {
        memberConsumptionMapper.updateById(consumption);
        return ApiResponse.success("修改成功");
    }

    /**
     * 删除消费记录
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        memberConsumptionMapper.deleteById(id);
        return ApiResponse.success("删除成功");
    }

    /**
     * 获取会员消费统计
     */
    @GetMapping("/statistics/{memberId}")
    public ApiResponse<Object> getStatistics(@PathVariable Long memberId) {
        // 总消费次数
        LambdaQueryWrapper<MemberConsumption> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberConsumption::getMemberId, memberId);
        wrapper.eq(MemberConsumption::getStatus, 1); // 已完成
        Long totalCount = memberConsumptionMapper.selectCount(wrapper);

        // 总消费金额
        List<MemberConsumption> consumptions = memberConsumptionMapper.selectList(wrapper);
        double totalAmount = consumptions.stream()
                .mapToDouble(c -> c.getActualAmount() != null ? c.getActualAmount() : 0)
                .sum();

        // 获得积分
        int totalPoints = consumptions.stream()
                .mapToInt(c -> c.getPointsEarned() != null ? c.getPointsEarned() : 0)
                .sum();

        // 本月消费
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberConsumption::getMemberId, memberId);
        wrapper.eq(MemberConsumption::getStatus, 1);
        wrapper.ge(MemberConsumption::getConsumptionTime, firstDayOfMonth);
        List<MemberConsumption> monthConsumptions = memberConsumptionMapper.selectList(wrapper);
        double monthAmount = monthConsumptions.stream()
                .mapToDouble(c -> c.getActualAmount() != null ? c.getActualAmount() : 0)
                .sum();

        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalCount", totalCount);
        statistics.put("totalAmount", totalAmount);
        statistics.put("totalPoints", totalPoints);
        statistics.put("monthAmount", monthAmount);

        return ApiResponse.success(statistics);
    }

    /**
     * 获取会员购买记录（含会员卡购买和普通消费）
     */
    @GetMapping("/purchases/{memberId}")
    public ApiResponse<List<Map<String, Object>>> getPurchases(@PathVariable Long memberId) {
        List<Map<String, Object>> purchaseList = new java.util.ArrayList<>();

        // 1. 会员卡购买记录
        LambdaQueryWrapper<MemberCard> cardWrapper = new LambdaQueryWrapper<>();
        cardWrapper.eq(MemberCard::getMemberId, memberId);
        cardWrapper.orderByDesc(MemberCard::getPurchaseDate);
        List<MemberCard> cards = memberCardMapper.selectList(cardWrapper);
        for (MemberCard card : cards) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", card.getCardId());
            item.put("orderNo", card.getCardNo());
            item.put("memberId", card.getMemberId());
            item.put("type", "card");
            item.put("typeName", "购卡");
            item.put("itemName", card.getCardTypeName());
            item.put("amount", card.getTotalAmount());
            item.put("actualAmount", card.getTotalAmount());
            item.put("time", card.getPurchaseDate());
            item.put("paymentMethod", "会员卡购买");
            item.put("status", card.getStatus());
            purchaseList.add(item);
        }

        // 2. 普通消费记录
        LambdaQueryWrapper<MemberConsumption> conWrapper = new LambdaQueryWrapper<>();
        conWrapper.eq(MemberConsumption::getMemberId, memberId);
        conWrapper.orderByDesc(MemberConsumption::getConsumptionTime);
        List<MemberConsumption> consumptions = memberConsumptionMapper.selectList(conWrapper);
        for (MemberConsumption con : consumptions) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", con.getConsumptionId());
            item.put("orderNo", con.getOrderNo());
            item.put("memberId", con.getMemberId());
            item.put("type", "consumption");
            item.put("typeName", con.getConsumptionTypeName());
            item.put("itemName", con.getConsumptionTypeName());
            item.put("amount", con.getAmount());
            item.put("actualAmount", con.getActualAmount());
            item.put("discountAmount", con.getDiscountAmount());
            item.put("time", con.getConsumptionTime());
            item.put("paymentMethod", con.getPaymentMethod());
            item.put("pointsEarned", con.getPointsEarned());
            item.put("status", con.getStatus());
            item.put("remarks", con.getRemarks());
            purchaseList.add(item);
        }

        // 按时间倒序
        purchaseList.sort((a, b) -> {
            Object t1 = a.get("time");
            Object t2 = b.get("time");
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.toString().compareTo(t1.toString());
        });

        return ApiResponse.success(purchaseList);
    }
}
