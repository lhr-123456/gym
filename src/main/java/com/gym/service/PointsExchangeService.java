package com.gym.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.PointsExchange;

import java.util.List;

public interface PointsExchangeService {

    Page<PointsExchange> getPageByMember(Long memberId, int pageNum, int pageSize);

    /** 后台分页查询所有兑换记录 */
    Page<PointsExchange> getPageAll(int pageNum, int pageSize, Long goodsId, String goodsName);

    /** 后台分页查询某商品的兑换记录 */
    List<PointsExchange> listByGoods(Long goodsId);

    List<PointsExchange> listByMember(Long memberId);

    PointsExchange getById(Long id);

    boolean exchange(PointsExchange exchange);

    boolean cancelExchange(Long id, Long memberId);

    /**
     * 会员兑换实物商品
     */
    String exchangeGoods(Long memberId, Long goodsId, Long refId);

    /**
     * 会员兑换课程（同时预约课程）
     */
    String exchangeCourse(Long memberId, Long refId, String goodsName, Integer points);
}
