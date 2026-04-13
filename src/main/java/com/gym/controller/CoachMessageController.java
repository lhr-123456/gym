package com.gym.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.dto.ApiResponse;
import com.gym.entity.CoachMessage;
import com.gym.service.CoachMessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/coach/message")
public class CoachMessageController {

    private final CoachMessageService coachMessageService;

    public CoachMessageController(CoachMessageService coachMessageService) {
        this.coachMessageService = coachMessageService;
    }

    /** 会员端：向教练发送消息 */
    @PostMapping("/send")
    public ApiResponse<String> sendMessage(@RequestBody CoachMessage message,
                                          HttpServletRequest request) {
        try {
            // 会员发送时，从 JWT 取 memberId（强校验，防止伪造）
            Long memberIdFromToken = (Long) request.getAttribute("memberId");
            if (memberIdFromToken != null) {
                message.setMemberId(memberIdFromToken);
            }
            if (message.getCoachId() == null) {
                return ApiResponse.error("教练ID不能为空");
            }
            if (message.getContent() == null || message.getContent().trim().isEmpty()) {
                return ApiResponse.error("消息内容不能为空");
            }
            message.setContent(message.getContent().trim());
            boolean ok = coachMessageService.save(message);
            if (ok) {
                return ApiResponse.success("消息已发送，教练会尽快回复您");
            }
            return ApiResponse.error("发送失败");
        } catch (Exception e) {
            return ApiResponse.error("发送失败：" + e.getMessage());
        }
    }

    /** 教练端：获取消息分页 */
    @GetMapping("/page")
    public ApiResponse<Page<CoachMessage>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        Long coachId = (Long) request.getAttribute("coachId");
        Page<CoachMessage> page = coachMessageService.getPage(pageNum, pageSize, coachId);
        return ApiResponse.success(page);
    }

    /** 教练端：获取消息列表 */
    @GetMapping("/list")
    public ApiResponse<Object> getList(HttpServletRequest request) {
        Long coachId = (Long) request.getAttribute("coachId");
        List<com.gym.entity.CoachMessage> list = coachMessageService.getList(coachId);
        return ApiResponse.success(list);
    }

    /** 教练端：未读消息数量 */
    @GetMapping("/unread-count")
    public ApiResponse<Integer> getUnreadCount(HttpServletRequest request) {
        Long coachId = (Long) request.getAttribute("coachId");
        int count = coachMessageService.getUnreadCount(coachId);
        return ApiResponse.success(count);
    }

    /** 教练端：标记单条已读 */
    @PutMapping("/read/{id}")
    public ApiResponse<String> markRead(@PathVariable Long id) {
        coachMessageService.markRead(id);
        return ApiResponse.success("已标记为已读");
    }

    /** 教练端：全部标为已读 */
    @PutMapping("/read-all")
    public ApiResponse<String> markAllRead(HttpServletRequest request) {
        Long coachId = (Long) request.getAttribute("coachId");
        coachMessageService.markAllRead(coachId);
        return ApiResponse.success("已全部标记为已读");
    }
}
