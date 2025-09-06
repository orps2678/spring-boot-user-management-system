package com.userms.controller;

import com.userms.common.ApiResponse;
import com.userms.common.ErrorCodes;
import com.userms.dto.AuthResponseDTO;
import com.userms.dto.UserDTO;
import com.userms.dto.UserLoginDTO;
import com.userms.dto.UserRegisterDTO;
import com.userms.exception.BusinessException;
import com.userms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "🔐 認證管理", description = "用戶註冊、登入、登出等認證相關功能")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "👤 用戶註冊",
            description = """
                    ## 註冊新用戶帳戶
                    
                    ### 功能說明
                    - 創建新的用戶帳戶
                    - 系統會自動驗證用戶名和信箱的唯一性
                    - 密碼會使用 BCrypt 進行安全加密
                    
                    ### 密碼要求
                    - 長度：8-100 字元
                    - 必須包含：大寫字母、小寫字母、數字、特殊字元
                    - 特殊字元包括：@$!%*?&
                    
                    ### 用戶名規則  
                    - 長度：3-50 字元
                    - 只能包含：字母、數字、底線
                    - 系統中必須唯一
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "註冊成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "註冊成功示例",
                                    value = """
                                            {
                                                "success": true,
                                                "message": "註冊成功",
                                                "data": {
                                                    "id": "550e8400-e29b-41d4-a716-446655440000",
                                                    "username": "john_doe",
                                                    "email": "john.doe@example.com",
                                                    "firstName": "John",
                                                    "lastName": "Doe",
                                                    "fullName": "John Doe",
                                                    "isActive": true,
                                                    "roles": [],
                                                    "createdTime": "2024-01-01 10:30:00",
                                                    "updatedTime": "2024-01-01 10:30:00"
                                                },
                                                "timestamp": "2024-01-01 10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "註冊失敗 - 參數驗證錯誤或業務規則違反",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "用戶名已存在",
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "使用者名稱已存在",
                                                        "errorCode": "USERNAME_EXISTS",
                                                        "timestamp": "2024-01-01 10:30:00"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "密碼不一致",
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "密碼與確認密碼不一致",
                                                        "errorCode": "PASSWORD_MISMATCH",
                                                        "timestamp": "2024-01-01 10:30:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/register")
    public ApiResponse<UserDTO> register(
            @Parameter(description = "用戶註冊資訊", required = true)
            @Valid @RequestBody UserRegisterDTO registerDTO) {
        UserDTO user = authService.register(registerDTO);
        return ApiResponse.success("註冊成功", user);
    }

    @Operation(
            summary = "🔑 用戶登入",
            description = """
                    ## 用戶帳戶登入
                    
                    ### 功能說明
                    - 支援使用用戶名或電子郵件登入
                    - 成功登入後會返回 JWT Token
                    - Token 有效期為 24 小時
                    
                    ### 使用方式
                    1. 輸入用戶名或電子郵件
                    2. 輸入密碼
                    3. 獲取返回的 JWT Token
                    4. 在後續 API 請求中攜帶此 Token
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "登入成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "登入成功示例",
                                    value = """
                                            {
                                                "success": true,
                                                "message": "登入成功",
                                                "data": {
                                                    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                    "tokenType": "Bearer",
                                                    "user": {
                                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                                        "username": "john_doe",
                                                        "email": "john.doe@example.com",
                                                        "firstName": "John",
                                                        "lastName": "Doe",
                                                        "fullName": "John Doe",
                                                        "isActive": true,
                                                        "roles": ["USER"],
                                                        "createdTime": "2024-01-01 10:30:00",
                                                        "updatedTime": "2024-01-01 10:30:00"
                                                    }
                                                },
                                                "timestamp": "2024-01-01 10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "登入失敗",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "用戶名或密碼錯誤",
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "用戶名或密碼錯誤",
                                                        "errorCode": "INVALID_CREDENTIALS",
                                                        "timestamp": "2024-01-01 10:30:00"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "帳戶已停用",
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "帳戶已被停用",
                                                        "errorCode": "USER_INACTIVE",
                                                        "timestamp": "2024-01-01 10:30:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/login")
    public ApiResponse<AuthResponseDTO> login(
            @Parameter(description = "用戶登入資訊", required = true)
            @Valid @RequestBody UserLoginDTO loginDTO) {
        AuthResponseDTO authResponse = authService.login(loginDTO);
        return ApiResponse.success("登入成功", authResponse);
    }

    @Operation(
            summary = "📋 獲取當前用戶資訊",
            description = """
                    ## 獲取當前登入用戶的詳細資訊
                    
                    ### 功能說明
                    - 需要攜帶有效的 JWT Token
                    - 返回當前登入用戶的完整資訊
                    - 包含用戶的角色權限列表
                    
                    ### 認證要求
                    - 請在 Authorization 欄位添加：`Bearer {your-jwt-token}`
                    """,
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "獲取成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "未授權 - Token 無效或已過期",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser(
            @Parameter(hidden = true) Principal principal) {
        UserDTO user = authService.getUserInfo(principal.getName());
        return ApiResponse.success(user);
    }

    @Operation(
            summary = "🚪 用戶登出",
            description = """
                    ## 用戶登出
                    
                    ### 功能說明
                    - 當前實現為無狀態登出
                    - 建議前端清除本地存儲的 Token
                    - 如需強制 Token 失效，可實現 Token 黑名單機制
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "登出成功",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "登出成功示例",
                                    value = """
                                            {
                                                "success": true,
                                                "message": "登出成功",
                                                "timestamp": "2024-01-01 10:30:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success("登出成功");
    }

    @Operation(
            summary = "🔄 刷新 JWT Token",
            description = """
                    ## JWT Token 刷新
                    
                    ### 功能說明
                    - 使用現有（可能已過期的）Token 獲取新的 Token
                    - 延長用戶的登入狀態而無需重新輸入密碼
                    - 新 Token 包含最新的用戶信息
                    
                    ### 使用場景
                    - Token 即將過期時主動刷新
                    - Token 已過期但仍在寬限期內（24小時）
                    - 保持用戶登入狀態的無縫體驗
                    
                    ### 安全考量
                    - 驗證原 Token 的有效性
                    - 檢查用戶帳戶狀態
                    - 限制刷新的時間窗口
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token 刷新成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "刷新成功示例",
                                    value = """
                                            {
                                                "success": true,
                                                "message": "Token 刷新成功",
                                                "data": {
                                                    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                    "tokenType": "Bearer",
                                                    "user": {
                                                        "id": "550e8400-e29b-41d4-a716-446655440000",
                                                        "username": "john_doe",
                                                        "email": "john.doe@example.com",
                                                        "firstName": "John",
                                                        "lastName": "Doe",
                                                        "fullName": "John Doe",
                                                        "isActive": true,
                                                        "roles": ["USER"],
                                                        "createdTime": "2024-01-01 10:30:00",
                                                        "updatedTime": "2024-01-01 10:30:00"
                                                    }
                                                },
                                                "timestamp": "2024-01-01 11:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Token 刷新失敗",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Token 已過期無法刷新",
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "Token 無法刷新，請重新登入",
                                                        "errorCode": "TOKEN_EXPIRED",
                                                        "timestamp": "2024-01-01 10:30:00"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "用戶帳戶已停用",
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "帳戶已被停用",
                                                        "errorCode": "USER_INACTIVE",
                                                        "timestamp": "2024-01-01 10:30:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/refresh")
    public ApiResponse<AuthResponseDTO> refreshToken(
            @Parameter(description = "原始 JWT Token", required = true)
            @RequestHeader("Authorization") String authorizationHeader) {
        
        // 從 Authorization header 提取 token
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BusinessException("缺少或無效的 Authorization header", ErrorCodes.TOKEN_MISSING);
        }
        
        String oldToken = authorizationHeader.substring(7);
        AuthResponseDTO authResponse = authService.refreshToken(oldToken);
        return ApiResponse.success("Token 刷新成功", authResponse);
    }
}