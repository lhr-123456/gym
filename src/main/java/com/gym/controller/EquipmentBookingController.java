package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.EquipmentBooking;
import com.gym.service.EquipmentBookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment-booking")
public class EquipmentBookingController {

    private final EquipmentBookingService equipmentBookingService;

    public EquipmentBookingController(EquipmentBookingService equipmentBookingService) {
        this.equipmentBookingService = equipmentBookingService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<EquipmentBooking>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            EquipmentBooking equipmentBooking) {
        Page<EquipmentBooking> page = new Page<>(pageNum, pageSize);
        Page<EquipmentBooking> result = equipmentBookingService.page(page);
        return ApiResponse.success(result);
    }

    @GetMapping("/list")
    public ApiResponse<List<EquipmentBooking>> getList(EquipmentBooking equipmentBooking) {
        List<EquipmentBooking> list = equipmentBookingService.list();
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<EquipmentBooking> getById(@PathVariable Long id) {
        EquipmentBooking equipmentBooking = equipmentBookingService.getById(id);
        if (equipmentBooking == null) {
            return ApiResponse.error("器材预约不存在");
        }
        return ApiResponse.success(equipmentBooking);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody EquipmentBooking equipmentBooking) {
        boolean result = equipmentBookingService.save(equipmentBooking);
        if (result) {
            return ApiResponse.success("添加器材预约成功");
        }
        return ApiResponse.error("添加器材预约失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody EquipmentBooking equipmentBooking) {
        boolean result = equipmentBookingService.updateById(equipmentBooking);
        if (result) {
            return ApiResponse.success("更新器材预约成功");
        }
        return ApiResponse.error("更新器材预约失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = equipmentBookingService.removeById(id);
        if (result) {
            return ApiResponse.success("删除器材预约成功");
        }
        return ApiResponse.error("删除器材预约失败");
    }
}
