package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.EquipmentInfo;
import com.gym.service.EquipmentInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

    /**
     * 上传器材图片，返回访问路径
     */
    @PostMapping("/upload")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ApiResponse.error("请选择要上传的图片");
        }
        // 仅允许图片格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
            (!originalFilename.toLowerCase().endsWith(".jpg") &&
             !originalFilename.toLowerCase().endsWith(".jpeg") &&
             !originalFilename.toLowerCase().endsWith(".png") &&
             !originalFilename.toLowerCase().endsWith(".gif") &&
             !originalFilename.toLowerCase().endsWith(".webp"))) {
            return ApiResponse.error("仅支持 jpg、png、gif、webp 格式的图片");
        }
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "equipment" + File.separator;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 生成唯一文件名：uuid + 原扩展名
            String ext = "";
            int dotIdx = originalFilename.lastIndexOf('.');
            if (dotIdx > 0) {
                ext = originalFilename.substring(dotIdx);
            }
            String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;
            File dest = new File(uploadDir + newFileName);
            file.transferTo(dest);
            // 返回访问路径，前端用 /uploads/equipment/xxx 访问
            return ApiResponse.success("uploads/equipment/" + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("图片上传失败：" + e.getMessage());
        }
    }
}
