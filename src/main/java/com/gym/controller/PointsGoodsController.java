package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.PointsGoods;
import com.gym.service.PointsGoodsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points/goods")
public class PointsGoodsController {

    private final PointsGoodsService pointsGoodsService;

    public PointsGoodsController(PointsGoodsService pointsGoodsService) {
        this.pointsGoodsService = pointsGoodsService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<PointsGoods>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            PointsGoods goods) {
        Page<PointsGoods> page = pointsGoodsService.getPage(pageNum, pageSize, goods);
        return ApiResponse.success(page);
    }

    @GetMapping("/list")
    public ApiResponse<List<PointsGoods>> getList(PointsGoods goods) {
        return ApiResponse.success(pointsGoodsService.listGoods(goods));
    }

    @GetMapping("/{id}")
    public ApiResponse<PointsGoods> getById(@PathVariable Long id) {
        PointsGoods goods = pointsGoodsService.getById(id);
        if (goods == null) {
            return ApiResponse.error("商品不存在");
        }
        return ApiResponse.success(goods);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody PointsGoods goods) {
        boolean ok = pointsGoodsService.save(goods);
        return ok ? ApiResponse.success("添加成功") : ApiResponse.error("添加失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody PointsGoods goods) {
        if (goods.getId() == null) {
            return ApiResponse.error("ID 不能为空");
        }
        boolean ok = pointsGoodsService.updateById(goods);
        return ok ? ApiResponse.success("更新成功") : ApiResponse.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean ok = pointsGoodsService.deleteById(id);
        return ok ? ApiResponse.success("删除成功") : ApiResponse.error("删除失败");
    }
}
