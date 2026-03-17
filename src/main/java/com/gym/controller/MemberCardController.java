package com.gym.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.MemberCard;
import com.gym.mapper.MemberCardMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/member/card")
public class MemberCardController {

    private final MemberCardMapper memberCardMapper;

    public MemberCardController(MemberCardMapper memberCardMapper) {
        this.memberCardMapper = memberCardMapper;
    }

    /**
     * 获取所有会员卡列表
     */
    @GetMapping("/list")
    public ApiResponse<List<MemberCard>> list() {
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MemberCard::getCreateTime);
        List<MemberCard> list = memberCardMapper.selectList(wrapper);
        return ApiResponse.success(list);
    }

    /**
     * 分页获取会员卡列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<MemberCard>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String cardType,
            @RequestParam(required = false) Integer status) {
        Page<MemberCard> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) {
            wrapper.eq(MemberCard::getMemberId, memberId);
        }
        if (cardType != null && !cardType.isEmpty()) {
            wrapper.eq(MemberCard::getCardType, cardType);
        }
        if (status != null) {
            wrapper.eq(MemberCard::getStatus, status);
        }
        wrapper.orderByDesc(MemberCard::getCreateTime);
        Page<MemberCard> result = memberCardMapper.selectPage(page, wrapper);
        return ApiResponse.success(result);
    }

    /**
     * 获取会员卡详情
     */
    @GetMapping("/{id}")
    public ApiResponse<MemberCard> getById(@PathVariable Long id) {
        MemberCard card = memberCardMapper.selectById(id);
        return ApiResponse.success(card);
    }

    /**
     * 根据会员ID获取会员卡列表
     */
    @GetMapping("/member/{memberId}")
    public ApiResponse<List<MemberCard>> getByMemberId(@PathVariable Long memberId) {
        LambdaQueryWrapper<MemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberCard::getMemberId, memberId);
        wrapper.orderByDesc(MemberCard::getCreateTime);
        List<MemberCard> list = memberCardMapper.selectList(wrapper);
        return ApiResponse.success(list);
    }

    /**
     * 新增会员卡
     */
    @PostMapping
    public ApiResponse<String> add(@RequestBody MemberCard memberCard) {
        // 生成卡号
        String cardNo = "C" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        memberCard.setCardNo(cardNo);

        // 设置初始值
        if (memberCard.getTotalTimes() == null) {
            memberCard.setTotalTimes(0);
        }
        if (memberCard.getUsedTimes() == null) {
            memberCard.setUsedTimes(0);
        }
        if (memberCard.getRemainingTimes() == null) {
            memberCard.setRemainingTimes(memberCard.getTotalTimes());
        }
        if (memberCard.getBalance() == null) {
            memberCard.setBalance(0.0);
        }
        if (memberCard.getPurchaseDate() == null) {
            memberCard.setPurchaseDate(LocalDate.now());
        }
        if (memberCard.getStatus() == null) {
            memberCard.setStatus(1); // 正常
        }

        memberCardMapper.insert(memberCard);
        return ApiResponse.success("添加成功");
    }

    /**
     * 修改会员卡
     */
    @PutMapping
    public ApiResponse<String> update(@RequestBody MemberCard memberCard) {
        memberCardMapper.updateById(memberCard);
        return ApiResponse.success("修改成功");
    }

    /**
     * 删除会员卡
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        memberCardMapper.deleteById(id);
        return ApiResponse.success("删除成功");
    }

    /**
     * 续费/延期
     */
    @PostMapping("/renew/{id}")
    public ApiResponse<String> renew(@PathVariable Long id, @RequestBody MemberCard renewData) {
        MemberCard card = memberCardMapper.selectById(id);
        if (card == null) {
            return ApiResponse.error("会员卡不存在");
        }

        // 更新卡的有效期
        if (renewData.getEndDate() != null) {
            card.setEndDate(renewData.getEndDate());
        }

        // 如果是充值，增加余额
        if (renewData.getBalance() != null && renewData.getBalance() > 0) {
            card.setBalance(card.getBalance() + renewData.getBalance());
        }

        // 如果是充值次数，增加次数
        if (renewData.getTotalTimes() != null && renewData.getTotalTimes() > 0) {
            card.setTotalTimes(card.getTotalTimes() + renewData.getTotalTimes());
            card.setRemainingTimes(card.getRemainingTimes() + renewData.getTotalTimes());
        }

        memberCardMapper.updateById(card);
        return ApiResponse.success("续费成功");
    }

    /**
     * 挂失
     */
    @PostMapping("/reportLoss/{id}")
    public ApiResponse<String> reportLoss(@PathVariable Long id) {
        MemberCard card = memberCardMapper.selectById(id);
        if (card == null) {
            return ApiResponse.error("会员卡不存在");
        }
        card.setStatus(2); // 挂失
        memberCardMapper.updateById(card);
        return ApiResponse.success("挂失成功");
    }

    /**
     * 解除挂失
     */
    @PostMapping("/unreportLoss/{id}")
    public ApiResponse<String> unreportLoss(@PathVariable Long id) {
        MemberCard card = memberCardMapper.selectById(id);
        if (card == null) {
            return ApiResponse.error("会员卡不存在");
        }
        card.setStatus(1); // 正常
        memberCardMapper.updateById(card);
        return ApiResponse.success("解除挂失成功");
    }

    /**
     * 补办
     */
    @PostMapping("/reissue/{id}")
    public ApiResponse<String> reissue(@PathVariable Long id) {
        MemberCard oldCard = memberCardMapper.selectById(id);
        if (oldCard == null) {
            return ApiResponse.error("会员卡不存在");
        }

        // 生成新卡号
        String newCardNo = "C" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        // 创建新卡，复制原卡信息
        MemberCard newCard = new MemberCard();
        newCard.setCardNo(newCardNo);
        newCard.setMemberId(oldCard.getMemberId());
        newCard.setCardType(oldCard.getCardType());
        newCard.setCardTypeName(oldCard.getCardTypeName());
        newCard.setTotalTimes(oldCard.getRemainingTimes());
        newCard.setUsedTimes(0);
        newCard.setRemainingTimes(oldCard.getRemainingTimes());
        newCard.setTotalAmount(oldCard.getBalance());
        newCard.setBalance(oldCard.getBalance());
        newCard.setPurchaseDate(LocalDate.now());
        newCard.setStartDate(LocalDate.now());
        newCard.setEndDate(oldCard.getEndDate());
        newCard.setStatus(1); // 正常

        // 禁用旧卡
        oldCard.setStatus(3); // 已补办
        memberCardMapper.updateById(oldCard);

        // 创建新卡
        memberCardMapper.insert(newCard);

        return ApiResponse.success("补办成功，新卡号：" + newCardNo);
    }

    /**
     * 使用次数（消费）
     */
    @PostMapping("/use/{id}")
    public ApiResponse<String> useCard(@PathVariable Long id) {
        MemberCard card = memberCardMapper.selectById(id);
        if (card == null) {
            return ApiResponse.error("会员卡不存在");
        }
        if (card.getStatus() != 1) {
            return ApiResponse.error("会员卡状态异常");
        }
        if (card.getRemainingTimes() != null && card.getRemainingTimes() <= 0) {
            return ApiResponse.error("次数已用完");
        }

        card.setUsedTimes(card.getUsedTimes() + 1);
        if (card.getRemainingTimes() != null) {
            card.setRemainingTimes(card.getRemainingTimes() - 1);
        }

        memberCardMapper.updateById(card);
        return ApiResponse.success("使用成功");
    }
}
