package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachCertificate;
import com.gym.service.CoachCertificateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coach-certificate")
public class CoachCertificateController {

    private final CoachCertificateService coachCertificateService;

    public CoachCertificateController(CoachCertificateService coachCertificateService) {
        this.coachCertificateService = coachCertificateService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<CoachCertificate>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            CoachCertificate coachCertificate) {
        Page<CoachCertificate> page = new Page<>(pageNum, pageSize);
        Page<CoachCertificate> result = coachCertificateService.page(page);
        return ApiResponse.success(result);
    }

    @GetMapping("/list")
    public ApiResponse<List<CoachCertificate>> getList(CoachCertificate coachCertificate) {
        List<CoachCertificate> list = coachCertificateService.list();
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<CoachCertificate> getById(@PathVariable Long id) {
        CoachCertificate coachCertificate = coachCertificateService.getById(id);
        if (coachCertificate == null) {
            return ApiResponse.error("证书不存在");
        }
        return ApiResponse.success(coachCertificate);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody CoachCertificate coachCertificate) {
        boolean result = coachCertificateService.save(coachCertificate);
        if (result) {
            return ApiResponse.success("添加证书成功");
        }
        return ApiResponse.error("添加证书失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody CoachCertificate coachCertificate) {
        boolean result = coachCertificateService.updateById(coachCertificate);
        if (result) {
            return ApiResponse.success("更新证书成功");
        }
        return ApiResponse.error("更新证书失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = coachCertificateService.removeById(id);
        if (result) {
            return ApiResponse.success("删除证书成功");
        }
        return ApiResponse.error("删除证书失败");
    }
}
