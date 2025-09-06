package com.userms.controller;

import com.userms.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "🧪 測試端點", description = "用於測試 JWT 認證功能的端點")
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(
        summary = "測試 JWT 認證",
        description = "測試 JWT Token 認證是否正常工作",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/jwt")
    public ApiResponse<Map<String, Object>> testJwt(Principal principal) {
        Map<String, Object> data = new HashMap<>();
        data.put("authenticated", true);
        data.put("username", principal.getName());
        data.put("timestamp", System.currentTimeMillis());
        return ApiResponse.success("JWT 認證測試成功", data);
    }
}