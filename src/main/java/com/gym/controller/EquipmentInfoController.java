package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.EquipmentInfo;
import com.gym.service.EquipmentInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentInfoController {

    private final EquipmentInfoService equipmentInfoService;

    public EquipmentInfoController(EquipmentInfoService equipmentInfoService) {
        this.equipmentInfoService = equipmentInfoService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<EquipmentInfo>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            EquipmentInfo equipmentInfo) {
        Page<EquipmentInfo> page = equipmentInfoService.getEquipmentPage(pageNum, pageSize, equipmentInfo);
        return ApiResponse.success(page);
    }

    @GetMapping("/list")
    public ApiResponse<List<EquipmentInfo>> getList(EquipmentInfo equipmentInfo) {
        List<EquipmentInfo> list = equipmentInfoService.list(equipmentInfo);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<EquipmentInfo> getById(@PathVariable Long id) {
        EquipmentInfo equipmentInfo = equipmentInfoService.getById(id);
        if (equipmentInfo == null) {
            return ApiResponse.error("器材不存在");
        }
        return ApiResponse.success(equipmentInfo);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody EquipmentInfo equipmentInfo) {
        boolean result = equipmentInfoService.save(equipmentInfo);
        if (result) {
            return ApiResponse.success("添加器材成功");
        }
        return ApiResponse.error("添加器材失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody EquipmentInfo equipmentInfo) {
        if (equipmentInfo.getEquipmentId() == null) {
            return ApiResponse.error("器材 ID 不能为空");
        }
        boolean result = equipmentInfoService.updateById(equipmentInfo);
        if (result) {
            return ApiResponse.success("更新器材成功");
        }
        return ApiResponse.error("更新器材失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = equipmentInfoService.deleteById(id);
        if (result) {
            return ApiResponse.success("删除器材成功");
        }
        return ApiResponse.error("删除器材失败");
    }
}
