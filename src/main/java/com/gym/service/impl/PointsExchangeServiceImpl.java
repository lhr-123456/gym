package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.MemberInfo;
import com.gym.entity.PointsExchange;
import com.gym.entity.PointsGoods;
import com.gym.mapper.PointsExchangeMapper;
import com.gym.service.CourseInfoService;
import com.gym.service.MemberInfoService;
import com.gym.service.PointsExchangeService;
import com.gym.service.PointsGoodsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointsExchangeServiceImpl implements PointsExchangeService {

    private final PointsExchangeMapper pointsExchangeMapper;
    private final PointsGoodsService pointsGoodsService;
    private final CourseInfoService courseInfoService;
    private final MemberInfoService memberInfoService;

    public PointsExchangeServiceImpl(PointsExchangeMapper pointsExchangeMapper,
                                     PointsGoodsService pointsGoodsService,
                                     CourseInfoService courseInfoService,
                                     MemberInfoService memberInfoService) {
        this.pointsExchangeMapper = pointsExchangeMapper;
        this.pointsGoodsService = pointsGoodsService;
        this.courseInfoService = courseInfoService;
        this.memberInfoService = memberInfoService;
    }

    @Override
    public Page<PointsExchange> getPageByMember(Long memberId, int pageNum, int pageSize) {
        Page<PointsExchange> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PointsExchange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsExchange::getMemberId, memberId)
               .orderByDesc(PointsExchange::getExchangeTime);
        return pointsExchangeMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<PointsExchange> getPageAll(int pageNum, int pageSize, Long goodsId, String goodsName) {
        Page<PointsExchange> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PointsExchange> wrapper = new LambdaQueryWrapper<>();
        if (goodsId != null) {
            wrapper.eq(PointsExchange::getGoodsId, goodsId);
        }
        if (goodsName != null && !goodsName.isEmpty()) {
            wrapper.like(PointsExchange::getGoodsName, goodsName);
        }
        wrapper.orderByDesc(PointsExchange::getExchangeTime);
        return pointsExchangeMapper.selectPage(page, wrapper);
    }

    @Override
    public List<PointsExchange> listByGoods(Long goodsId) {
        LambdaQueryWrapper<PointsExchange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(goodsId != null, PointsExchange::getGoodsId, goodsId)
               .orderByDesc(PointsExchange::getExchangeTime);
        return pointsExchangeMapper.selectList(wrapper);
    }

    @Override
    public List<PointsExchange> listByMember(Long memberId) {
        LambdaQueryWrapper<PointsExchange> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsExchange::getMemberId, memberId)
               .orderByDesc(PointsExchange::getExchangeTime);
        return pointsExchangeMapper.selectList(wrapper);
    }

    @Override
    public PointsExchange getById(Long id) {
        return pointsExchangeMapper.selectById(id);
    }

    @Override
    public boolean exchange(PointsExchange exchange) {
        if (exchange.getExchangeTime() == null) {
            exchange.setExchangeTime(LocalDateTime.now());
        }
        if (exchange.getStatus() == null) {
            exchange.setStatus(1); // 已完成
        }
        return pointsExchangeMapper.insert(exchange) > 0;
    }

    @Override
    public boolean cancelExchange(Long id, Long memberId) {
        PointsExchange exchange = pointsExchangeMapper.selectById(id);
        if (exchange == null || !exchange.getMemberId().equals(memberId)) {
            return false;
        }
        exchange.setStatus(2); // 已取消
        return pointsExchangeMapper.updateById(exchange) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String exchangeGoods(Long memberId, Long goodsId, Long refId) {
        // 1. 查询商品
        PointsGoods goods = pointsGoodsService.getById(goodsId);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        if (goods.getStatus() == 0 || goods.getStock() <= 0) {
            throw new RuntimeException("商品已下架或库存不足");
        }

        // 2. 查询会员
        MemberInfo member = memberInfoService.getById(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }
        int currentPoints = member.getPoints() != null ? member.getPoints() : 0;
        int needPoints = goods.getPoints() != null ? goods.getPoints() : 0;
        if (currentPoints < needPoints) {
            throw new RuntimeException("积分不足，当前积分：" + currentPoints + "，需要：" + needPoints);
        }

        // 3. 扣减库存
        if (!pointsGoodsService.reduceStock(goods.getId(), 1)) {
            throw new RuntimeException("库存扣减失败");
        }

        // 4. 扣减积分
        member.setPoints(currentPoints - needPoints);
        if (!memberInfoService.updateById(member)) {
            throw new RuntimeException("积分扣减失败");
        }

        // 5. 记录兑换
        PointsExchange exchange = new PointsExchange();
        exchange.setMemberId(memberId);
        exchange.setGoodsId(goodsId);
        exchange.setRefId(refId);
        exchange.setGoodsName(goods.getName());
        exchange.setPoints(needPoints);
        exchange.setStatus(1);
        exchange.setExchangeTime(LocalDateTime.now());
        if (pointsExchangeMapper.insert(exchange) <= 0) {
            throw new RuntimeException("兑换记录保存失败");
        }

        return "ok";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String exchangeCourse(Long memberId, Long refId, String goodsName, Integer points) {
        // 1. 查询会员
        MemberInfo member = memberInfoService.getById(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }
        int currentPoints = member.getPoints() != null ? member.getPoints() : 0;
        int needPoints = points != null ? points : 0;
        if (currentPoints < needPoints) {
            throw new RuntimeException("积分不足，当前积分：" + currentPoints + "，需要：" + needPoints);
        }

        // 2. 预约课程（bookCourse 会抛出异常，触发事务回滚）
        if (refId != null) {
            boolean booked = courseInfoService.bookCourse(refId, memberId, null);
            if (!booked) {
                throw new RuntimeException("课程预约失败");
            }
        }

        // 3. 扣减积分
        member.setPoints(currentPoints - needPoints);
        if (!memberInfoService.updateById(member)) {
            throw new RuntimeException("积分扣减失败");
        }

        // 4. 记录兑换
        PointsExchange exchange = new PointsExchange();
        exchange.setMemberId(memberId);
        exchange.setRefId(refId);
        exchange.setGoodsName(goodsName);
        exchange.setPoints(needPoints);
        exchange.setStatus(1);
        exchange.setExchangeTime(LocalDateTime.now());
        if (pointsExchangeMapper.insert(exchange) <= 0) {
            throw new RuntimeException("兑换记录保存失败");
        }

        return "ok";
    }
}
