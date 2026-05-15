package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.MemberMessage;
import com.gym.service.MemberMessageService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/member/message")
public class MemberMessageController {

    private final MemberMessageService memberMessageService;

    public MemberMessageController(MemberMessageService memberMessageService) {
        this.memberMessageService = memberMessageService;
    }

    /**
     * 分页获取会员消息列表
     */
    @GetMapping("/page")
    public ApiResponse<Page<MemberMessage>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        Page<MemberMessage> page = memberMessageService.getPage(pageNum, pageSize, memberId, type);
        return ApiResponse.success(page);
    }

    /**
     * 获取会员消息列表
     */
    @GetMapping("/list")
    public ApiResponse<Object> getList(
            @RequestParam(required = false) String type,
            HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        List<com.gym.entity.MemberMessage> list = memberMessageService.getList(memberId, type);
        return ApiResponse.success(list);
    }

    /**
     * 获取会员未读消息数量
     */
    @GetMapping("/unread-count")
    public ApiResponse<Integer> getUnreadCount(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        int count = memberMessageService.getUnreadCount(memberId);
        return ApiResponse.success(count);
    }

    /**
     * 标记单条消息为已读
     */
    @PutMapping("/read/{id}")
    public ApiResponse<String> markRead(@PathVariable Long id) {
        memberMessageService.markRead(id);
        return ApiResponse.success("已标记为已读");
    }

    /**
     * 标记全部消息为已读
     */
    @PutMapping("/read-all")
    public ApiResponse<String> markAllRead(HttpServletRequest request) {
        Long memberId = (Long) request.getAttribute("memberId");
        memberMessageService.markAllRead(memberId);
        return ApiResponse.success("已全部标记为已读");
    }
}
