package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.MemberInfo;
import com.gym.service.MemberInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberInfoController {

    private final MemberInfoService memberInfoService;

    public MemberInfoController(MemberInfoService memberInfoService) {
        this.memberInfoService = memberInfoService;
    }

    @GetMapping("/page")
    public ApiResponse<Page<MemberInfo>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            MemberInfo memberInfo) {
        Page<MemberInfo> page = memberInfoService.getMemberPage(pageNum, pageSize, memberInfo);
        return ApiResponse.success(page);
    }

    @GetMapping("/list")
    public ApiResponse<List<MemberInfo>> getList(MemberInfo memberInfo) {
        List<MemberInfo> list = memberInfoService.list(memberInfo);
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}")
    public ApiResponse<MemberInfo> getById(@PathVariable Long id) {
        MemberInfo memberInfo = memberInfoService.getById(id);
        if (memberInfo == null) {
            return ApiResponse.error("会员不存在");
        }
        return ApiResponse.success(memberInfo);
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody MemberInfo memberInfo) {
        boolean result = memberInfoService.save(memberInfo);
        if (result) {
            return ApiResponse.success("添加会员成功");
        }
        return ApiResponse.error("添加会员失败");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody MemberInfo memberInfo) {
        if (memberInfo.getMemberId() == null) {
            return ApiResponse.error("会员 ID 不能为空");
        }
        boolean result = memberInfoService.updateById(memberInfo);
        if (result) {
            return ApiResponse.success("更新会员成功");
        }
        return ApiResponse.error("更新会员失败");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        boolean result = memberInfoService.deleteById(id);
        if (result) {
            return ApiResponse.success("删除会员成功");
        }
        return ApiResponse.error("删除会员失败");
    }
}
