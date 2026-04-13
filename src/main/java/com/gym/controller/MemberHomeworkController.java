package com.gym.controller;

import com.gym.dto.ApiResponse;
import com.gym.entity.CoachHomework;
import com.gym.service.CoachHomeworkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member/homework")
public class MemberHomeworkController {

    private final CoachHomeworkService homeworkService;

    public MemberHomeworkController(CoachHomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    /** 获取我的所有作业 */
    @GetMapping("/list")
    public ApiResponse<List<CoachHomework>> getList(@RequestParam Long memberId) {
        return ApiResponse.success(homeworkService.listByMember(memberId));
    }

    /** 获取待完成的作业 */
    @GetMapping("/pending")
    public ApiResponse<List<CoachHomework>> getPending(@RequestParam Long memberId) {
        return ApiResponse.success(homeworkService.listPendingByMember(memberId));
    }

    /** 作业打卡（标记完成） */
    @PutMapping("/complete/{id}")
    public ApiResponse<String> complete(@PathVariable Long id, @RequestParam Long memberId) {
        boolean ok = homeworkService.complete(id, memberId);
        if (ok) {
            return ApiResponse.success("打卡成功！继续加油！");
        }
        return ApiResponse.error("打卡失败，记录不存在或无权操作");
    }
}
