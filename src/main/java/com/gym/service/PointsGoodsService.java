package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.PointsGoods;

import java.util.List;

public interface PointsGoodsService {

    Page<PointsGoods> getPage(int pageNum, int pageSize, PointsGoods goods);

    List<PointsGoods> listGoods(PointsGoods goods);

    PointsGoods getById(Long id);

    boolean save(PointsGoods goods);

    boolean updateById(PointsGoods goods);

    boolean deleteById(Long id);

    boolean reduceStock(Long id, int count);
}
