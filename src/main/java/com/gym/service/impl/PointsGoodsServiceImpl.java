package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.PointsGoods;
import com.gym.mapper.PointsGoodsMapper;
import com.gym.service.PointsGoodsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PointsGoodsServiceImpl implements PointsGoodsService {

    private final PointsGoodsMapper pointsGoodsMapper;

    public PointsGoodsServiceImpl(PointsGoodsMapper pointsGoodsMapper) {
        this.pointsGoodsMapper = pointsGoodsMapper;
    }

    @Override
    public Page<PointsGoods> getPage(int pageNum, int pageSize, PointsGoods goods) {
        Page<PointsGoods> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PointsGoods> wrapper = buildQueryWrapper(goods);
        return pointsGoodsMapper.selectPage(page, wrapper);
    }

    @Override
    public List<PointsGoods> listGoods(PointsGoods goods) {
        LambdaQueryWrapper<PointsGoods> wrapper = buildQueryWrapper(goods);
        return pointsGoodsMapper.selectList(wrapper);
    }

    @Override
    public PointsGoods getById(Long id) {
        return pointsGoodsMapper.selectById(id);
    }

    @Override
    public boolean save(PointsGoods goods) {
        if (goods.getStock() == null) {
            goods.setStock(0);
        }
        if (goods.getStatus() == null) {
            goods.setStatus(1);
        }
        return pointsGoodsMapper.insert(goods) > 0;
    }

    @Override
    public boolean updateById(PointsGoods goods) {
        return pointsGoodsMapper.updateById(goods) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return pointsGoodsMapper.deleteById(id) > 0;
    }

    @Override
    public boolean reduceStock(Long id, int count) {
        PointsGoods goods = pointsGoodsMapper.selectById(id);
        if (goods == null || goods.getStock() < count) {
            return false;
        }
        goods.setStock(goods.getStock() - count);
        return pointsGoodsMapper.updateById(goods) > 0;
    }

    private LambdaQueryWrapper<PointsGoods> buildQueryWrapper(PointsGoods goods) {
        LambdaQueryWrapper<PointsGoods> wrapper = new LambdaQueryWrapper<>();
        if (goods != null) {
            wrapper.eq(goods.getId() != null, PointsGoods::getId, goods.getId())
                   .like(StringUtils.hasText(goods.getName()), PointsGoods::getName, goods.getName())
                   .eq(StringUtils.hasText(goods.getType()), PointsGoods::getType, goods.getType())
                   .eq(goods.getStatus() != null, PointsGoods::getStatus, goods.getStatus())
                   .ge(goods.getPoints() != null, PointsGoods::getPoints, goods.getPoints());
        }
        wrapper.orderByDesc(PointsGoods::getCreateTime);
        return wrapper;
    }
}
