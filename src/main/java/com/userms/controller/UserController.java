package com.userms.controller;

import com.userms.common.ApiResponse;
import com.userms.common.PageResult;
import com.userms.dto.UserDTO;
import com.userms.dto.UserRegisterDTO;
import com.userms.service.UserService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "👥 用戶管理", description = "用戶資料的 CRUD 操作和角色管理")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "📋 獲取用戶列表",
            description = """
                    ## 分頁獲取所有用戶列表
                    
                    ### 功能說明
                    - 支持分頁查詢
                    - 默認按創建時間倒序排列
                    - 包含用戶的基本信息和角色列表
                    
                    ### 參數說明
                    - page: 頁碼，從 0 開始
                    - size: 每頁數量，默認 10
                    - sort: 排序字段，支持 createdTime, username 等
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "獲取成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageResult.class),
                            examples = @ExampleObject(
                                    name = "用戶列表示例",
                                    value = """
                                            {
                                                "success": true,
                                                "message": "查詢成功",
                                                "data": {
                                                    "content": [
                                                        {
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
                                                    ],
                                                    "page": 0,
                                                    "size": 10,
                                                    "totalElements": 1,
                                                    "totalPages": 1
                                                },
                                                "timestamp": "2024-01-01 10:30:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ApiResponse<PageResult<UserDTO>> getAllUsers(
            @Parameter(description = "頁碼 (從0開始)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "createdTime")
            @RequestParam(defaultValue = "createdTime") String sortBy,
            @Parameter(description = "排序方向 (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResult<UserDTO> result = userService.getAllUsers(pageable);
        return ApiResponse.success("查詢成功", result);
    }

    @Operation(
            summary = "🔍 根據 ID 獲取用戶詳情",
            description = """
                    ## 獲取指定用戶的詳細資訊
                    
                    ### 功能說明
                    - 根據用戶 ID 獲取詳細信息
                    - 包含用戶的所有角色信息
                    - 需要有效的 JWT Token
                    """)
    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        return ApiResponse.success(user);
    }

    @Operation(
            summary = "🔎 搜索用戶",
            description = """
                    ## 根據關鍵字搜索用戶
                    
                    ### 搜索範圍
                    - 用戶名
                    - 電子郵件
                    - 姓名（名字和姓氏）
                    
                    ### 搜索特點
                    - 支持模糊匹配
                    - 不區分大小寫
                    - 返回所有匹配的用戶
                    """)
    @GetMapping("/search")
    public ApiResponse<List<UserDTO>> searchUsers(
            @Parameter(description = "搜索關鍵字", required = true, example = "john")
            @RequestParam String keyword) {
        List<UserDTO> users = userService.searchUsers(keyword);
        return ApiResponse.success("搜索成功", users);
    }

    @Operation(
            summary = "➕ 創建新用戶",
            description = """
                    ## 創建新用戶帳戶
                    
                    ### 功能說明
                    - 管理員創建新用戶
                    - 自動驗證用戶名和郵箱唯一性
                    - 密碼自動加密存儲
                    
                    ### 注意事項
                    - 需要管理員權限
                    - 新用戶默認為啟用狀態
                    - 創建後需要手動分配角色
                    """)
    @PostMapping
    public ApiResponse<UserDTO> createUser(
            @Parameter(description = "用戶創建資訊", required = true)
            @Valid @RequestBody UserRegisterDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ApiResponse.success("用戶創建成功", createdUser);
    }

    @Operation(
            summary = "✏️ 更新用戶資訊",
            description = """
                    ## 更新指定用戶的基本資訊
                    
                    ### 可更新字段
                    - 用戶名（需檢查唯一性）
                    - 電子郵件（需檢查唯一性）
                    - 姓名
                    - 啟用/停用狀態
                    
                    ### 注意事項
                    - 不能更新密碼（請使用專門的密碼重置接口）
                    - 更新用戶名或郵箱會進行唯一性驗證
                    """)
    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable String id,
            @Parameter(description = "用戶更新資訊", required = true)
            @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ApiResponse.success("用戶更新成功", updatedUser);
    }

    @Operation(
            summary = "✅ 啟用用戶",
            description = """
                    ## 啟用指定用戶帳戶
                    
                    ### 功能說明
                    - 將用戶狀態設為啟用
                    - 啟用後用戶可以正常登入
                    - 需要管理員權限
                    """)
    @PostMapping("/{id}/enable")
    public ApiResponse<Void> enableUser(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable String id) {
        userService.enableUser(id);
        return ApiResponse.success("用戶啟用成功");
    }

    @Operation(
            summary = "❌ 停用用戶",
            description = """
                    ## 停用指定用戶帳戶
                    
                    ### 功能說明
                    - 將用戶狀態設為停用
                    - 停用後用戶無法登入
                    - 現有 Token 仍然有效直到過期
                    - 需要管理員權限
                    """)
    @PostMapping("/{id}/disable")
    public ApiResponse<Void> disableUser(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable String id) {
        userService.disableUser(id);
        return ApiResponse.success("用戶停用成功");
    }

    @Operation(
            summary = "🗑️ 刪除用戶",
            description = """
                    ## 永久刪除用戶帳戶
                    
                    ### 功能說明
                    - 永久刪除用戶及其關聯數據
                    - 刪除用戶的所有角色關聯
                    - 此操作不可逆，請謹慎使用
                    
                    ### 安全考量
                    - 需要最高管理員權限
                    - 建議改為停用而非刪除
                    """)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable String id) {
        userService.deleteUser(id);
        return ApiResponse.success("用戶刪除成功");
    }

    @Operation(
            summary = "🎭 分配角色給用戶",
            description = """
                    ## 為指定用戶分配角色
                    
                    ### 功能說明
                    - 為用戶添加新的角色
                    - 支持多角色並存
                    - 會檢查角色是否已存在
                    
                    ### 參數說明
                    - roleCode: 角色代碼，如 "ADMIN", "USER" 等
                    """)
    @PostMapping("/{id}/roles/{roleCode}")
    public ApiResponse<Void> assignRole(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable String id,
            @Parameter(description = "角色代碼", required = true, example = "USER")
            @PathVariable String roleCode) {
        userService.assignRole(id, roleCode);
        return ApiResponse.success("角色分配成功");
    }

    @Operation(
            summary = "🚫 撤銷用戶角色",
            description = """
                    ## 撤銷用戶的指定角色
                    
                    ### 功能說明
                    - 移除用戶的指定角色
                    - 不會刪除其他角色
                    - 會檢查用戶是否擁有該角色
                    """)
    @DeleteMapping("/{id}/roles/{roleCode}")
    public ApiResponse<Void> revokeRole(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable String id,
            @Parameter(description = "角色代碼", required = true, example = "USER")
            @PathVariable String roleCode) {
        userService.revokeRole(id, roleCode);
        return ApiResponse.success("角色撤銷成功");
    }

    @Operation(
            summary = "📝 獲取用戶角色列表",
            description = """
                    ## 獲取指定用戶的所有角色
                    
                    ### 功能說明
                    - 返回用戶當前擁有的所有有效角色
                    - 只返回角色代碼列表
                    - 可用於權限檢查
                    """)
    @GetMapping("/{id}/roles")
    public ApiResponse<List<String>> getUserRoles(
            @Parameter(description = "用戶 ID", required = true)
            @PathVariable String id) {
        List<String> roles = userService.getUserRoles(id);
        return ApiResponse.success("角色獲取成功", roles);
    }
}