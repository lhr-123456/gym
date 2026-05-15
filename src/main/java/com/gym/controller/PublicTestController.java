package com.gym.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PublicTestController {
    /**
     * 公开测试接口，返回问候消息
     */
    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from backend!");
        response.put("status", "success");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }
    
    /**
     * 回显接收到的数据
     */
    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
