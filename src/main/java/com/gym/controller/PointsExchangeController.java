package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.PointsExchange;
import com.gym.entity.PointsGoods;
import com.gym.service.PointsExchangeService;
import com.gym.service.PointsGoodsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points/exchange")
public class PointsExchangeController {

    private final PointsExchangeService pointsExchangeService;
    private final PointsGoodsService pointsGoodsService;

    public PointsExchangeController(PointsExchangeService pointsExchangeService,
                                    PointsGoodsService pointsGoodsService) {
        this.pointsExchangeService = pointsExchangeService;
        this.pointsGoodsService = pointsGoodsService;
    }

    /** 会员分页查询自己的兑换记录 */
    @GetMapping("/page")
    public ApiResponse<Page<PointsExchange>> getMyPage(
            @RequestParam Long memberId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<PointsExchange> page = pointsExchangeService.getPageByMember(memberId, pageNum, pageSize);
        return ApiResponse.success(page);
    }

    /** 会员查询自己的兑换记录列表 */
    @GetMapping("/list")
    public ApiResponse<List<PointsExchange>> getMyList(@RequestParam Long memberId) {
        return ApiResponse.success(pointsExchangeService.listByMember(memberId));
    }

    /** 会员兑换商品 */
    @PostMapping("/goods")
    public ApiResponse<String> exchangeGoods(@RequestBody PointsExchange exchange) {
        String result = pointsExchangeService.exchangeGoods(
                exchange.getMemberId(),
                exchange.getGoodsId(),
                exchange.getRefId()
        );
        if ("ok".equals(result)) {
            return ApiResponse.success("兑换成功！请到前台领取商品");
        }
        return ApiResponse.error(result);
    }

    /** 会员兑换课程 */
    @PostMapping("/course")
    public ApiResponse<String> exchangeCourse(@RequestBody PointsExchange exchange) {
        String result = pointsExchangeService.exchangeCourse(
                exchange.getMemberId(),
                exchange.getRefId(),
                exchange.getGoodsName(),
                exchange.getPoints()
        );
        if ("ok".equals(result)) {
            return ApiResponse.success("兑换成功！课程已添加到您的预约中");
        }
        return ApiResponse.error(result);
    }

    /** 取消兑换 */
    @PutMapping("/cancel/{id}")
    public ApiResponse<String> cancel(@PathVariable Long id, @RequestParam Long memberId) {
        boolean ok = pointsExchangeService.cancelExchange(id, memberId);
        if (!ok) {
            return ApiResponse.error("取消失败，记录不存在或无权操作");
        }
        return ApiResponse.success("已取消");
    }

    /** 后台分页查询所有兑换记录 */
    @GetMapping("/admin/page")
    public ApiResponse<Page<PointsExchange>> getAdminPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long goodsId,
            @RequestParam(required = false) String goodsName) {
        Page<PointsExchange> page = pointsExchangeService.getPageAll(pageNum, pageSize, goodsId, goodsName);
        return ApiResponse.success(page);
    }

    /** 查询某商品的兑换记录（按 goodsId 筛选） */
    @GetMapping("/list/by-goods")
    public ApiResponse<List<PointsExchange>> getListByGoods(@RequestParam Long goodsId) {
        return ApiResponse.success(pointsExchangeService.listByGoods(goodsId));
    }
}
