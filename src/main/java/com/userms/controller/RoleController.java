package com.userms.controller;

import com.userms.common.ApiResponse;
import com.userms.common.PageResult;
import com.userms.dto.RoleCreateDTO;
import com.userms.dto.RoleDTO;
import com.userms.service.RoleService;
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

@Tag(name = "🎭 角色管理", description = "系統角色的 CRUD 操作和權限分配")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class RoleController {

    private final RoleService roleService;

    @Operation(
            summary = "📋 獲取角色列表",
            description = """
                    ## 分頁獲取所有角色列表
                    
                    ### 功能說明
                    - 支持分頁查詢
                    - 默認按創建時間倒序排列
                    - 包含角色的基本信息、權限數量和用戶數量
                    
                    ### 參數說明
                    - page: 頁碼，從 0 開始
                    - size: 每頁數量，默認 10
                    - sort: 排序字段，支持 createdTime, roleName, roleCode 等
                    """)
    @GetMapping
    public ApiResponse<PageResult<RoleDTO>> getAllRoles(
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
        
        PageResult<RoleDTO> result = roleService.getAllRoles(pageable);
        return ApiResponse.success("查詢成功", result);
    }

    @Operation(
            summary = "✅ 獲取啟用角色列表",
            description = """
                    ## 獲取所有啟用狀態的角色
                    
                    ### 功能說明
                    - 只返回啟用狀態的角色
                    - 常用於下拉選單或角色分配
                    - 支持分頁查詢
                    """)
    @GetMapping("/active")
    public ApiResponse<PageResult<RoleDTO>> getActiveRoles(
            @Parameter(description = "頁碼 (從0開始)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("roleName").ascending());
        PageResult<RoleDTO> result = roleService.getActiveRoles(pageable);
        return ApiResponse.success("查詢成功", result);
    }

    @Operation(
            summary = "🔍 根據 ID 獲取角色詳情",
            description = """
                    ## 獲取指定角色的詳細資訊
                    
                    ### 功能說明
                    - 根據角色 ID 獲取詳細信息
                    - 包含角色的所有權限信息
                    - 顯示使用該角色的用戶數量
                    """)
    @GetMapping("/{id}")
    public ApiResponse<RoleDTO> getRoleById(
            @Parameter(description = "角色 ID", required = true)
            @PathVariable String id) {
        RoleDTO role = roleService.getRoleById(id);
        return ApiResponse.success(role);
    }

    @Operation(
            summary = "🔎 根據代碼獲取角色",
            description = """
                    ## 根據角色代碼獲取角色資訊
                    
                    ### 功能說明
                    - 使用角色代碼（如 ADMIN, USER）查詢
                    - 適用於基於角色代碼的權限檢查
                    - 返回完整的角色信息
                    """)
    @GetMapping("/code/{roleCode}")
    public ApiResponse<RoleDTO> getRoleByCode(
            @Parameter(description = "角色代碼", required = true, example = "ADMIN")
            @PathVariable String roleCode) {
        RoleDTO role = roleService.getRoleByCode(roleCode);
        return ApiResponse.success(role);
    }

    @Operation(
            summary = "🔎 搜索角色",
            description = """
                    ## 根據關鍵字搜索角色
                    
                    ### 搜索範圍
                    - 角色名稱
                    - 角色代碼
                    - 角色描述
                    
                    ### 搜索特點
                    - 支持模糊匹配
                    - 不區分大小寫
                    - 返回所有匹配的角色
                    """)
    @GetMapping("/search")
    public ApiResponse<List<RoleDTO>> searchRoles(
            @Parameter(description = "搜索關鍵字", required = true, example = "admin")
            @RequestParam String keyword) {
        List<RoleDTO> roles = roleService.searchRoles(keyword);
        return ApiResponse.success("搜索成功", roles);
    }

    @Operation(
            summary = "➕ 創建新角色",
            description = """
                    ## 創建新的系統角色
                    
                    ### 功能說明
                    - 創建新的角色定義
                    - 自動驗證角色名稱和代碼唯一性
                    - 新角色默認為啟用狀態
                    
                    ### 角色代碼規則
                    - 必須以大寫字母開頭
                    - 只能包含大寫字母、數字和底線
                    - 建議使用語義化命名，如：ADMIN、USER、MANAGER
                    """)
    @PostMapping
    public ApiResponse<RoleDTO> createRole(
            @Parameter(description = "角色創建資訊", required = true)
            @Valid @RequestBody RoleCreateDTO roleDTO) {
        RoleDTO createdRole = roleService.createRole(roleDTO);
        return ApiResponse.success("角色創建成功", createdRole);
    }

    @Operation(
            summary = "✏️ 更新角色資訊",
            description = """
                    ## 更新指定角色的基本資訊
                    
                    ### 可更新字段
                    - 角色名稱（需檢查唯一性）
                    - 角色代碼（需檢查唯一性）
                    - 角色描述
                    - 啟用/停用狀態
                    
                    ### 注意事項
                    - 更新角色代碼可能影響現有的權限檢查邏輯
                    - 停用角色不會影響已分配給用戶的角色
                    """)
    @PutMapping("/{id}")
    public ApiResponse<RoleDTO> updateRole(
            @Parameter(description = "角色 ID", required = true)
            @PathVariable String id,
            @Parameter(description = "角色更新資訊", required = true)
            @Valid @RequestBody RoleDTO roleDTO) {
        RoleDTO updatedRole = roleService.updateRole(id, roleDTO);
        return ApiResponse.success("角色更新成功", updatedRole);
    }

    @Operation(
            summary = "✅ 啟用角色",
            description = """
                    ## 啟用指定角色
                    
                    ### 功能說明
                    - 將角色狀態設為啟用
                    - 啟用後角色可以被分配給用戶
                    - 需要管理員權限
                    """)
    @PostMapping("/{id}/enable")
    public ApiResponse<Void> enableRole(
            @Parameter(description = "角色 ID", required = true)
            @PathVariable String id) {
        roleService.enableRole(id);
        return ApiResponse.success("角色啟用成功");
    }

    @Operation(
            summary = "❌ 停用角色",
            description = """
                    ## 停用指定角色
                    
                    ### 功能說明
                    - 將角色狀態設為停用
                    - 停用後角色無法分配給新用戶
                    - 已擁有此角色的用戶不受影響
                    - 需要管理員權限
                    """)
    @PostMapping("/{id}/disable")
    public ApiResponse<Void> disableRole(
            @Parameter(description = "角色 ID", required = true)
            @PathVariable String id) {
        roleService.disableRole(id);
        return ApiResponse.success("角色停用成功");
    }

    @Operation(
            summary = "🗑️ 刪除角色",
            description = """
                    ## 永久刪除角色
                    
                    ### 功能說明
                    - 永久刪除角色及其關聯數據
                    - 刪除角色的所有權限關聯
                    - 如果有用戶使用此角色，則無法刪除
                    
                    ### 安全考量
                    - 需要最高管理員權限
                    - 建議改為停用而非刪除
                    - 刪除前會檢查是否有用戶使用
                    """)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(
            @Parameter(description = "角色 ID", required = true)
            @PathVariable String id) {
        roleService.deleteRole(id);
        return ApiResponse.success("角色刪除成功");
    }

    @Operation(
            summary = "🔑 分配權限給角色",
            description = """
                    ## 為指定角色分配權限
                    
                    ### 功能說明
                    - 為角色添加新的權限
                    - 支持多權限並存
                    - 會檢查權限是否已存在
                    
                    ### 參數說明
                    - permissionCode: 權限代碼，如 "USER_READ", "USER_WRITE" 等
                    """)
    @PostMapping("/{id}/permissions/{permissionCode}")
    public ApiResponse<Void> assignPermission(
            @Parameter(description = "角色 ID", required = true)
            @PathVariable String id,
            @Parameter(description = "權限代碼", required = true, example = "USER_READ")
            @PathVariable String permissionCode) {
        roleService.assignPermission(id, permissionCode);
        return ApiResponse.success("權限分配成功");
    }

    @Operation(
            summary = "🚫 撤銷角色權限",
            description = """
                    ## 撤銷角色的指定權限
                    
                    ### 功能說明
                    - 移除角色的指定權限
                    - 不會影響其他權限
                    - 會檢查角色是否擁有該權限
                    """)
    @DeleteMapping("/{id}/permissions/{permissionCode}")
    public ApiResponse<Void> revokePermission(
            @Parameter(description = "角色 ID", required = true)
            @PathVariable String id,
            @Parameter(description = "權限代碼", required = true, example = "USER_READ")
            @PathVariable String permissionCode) {
        roleService.revokePermission(id, permissionCode);
        return ApiResponse.success("權限撤銷成功");
    }

    @Operation(
            summary = "📝 獲取角色權限列表",
            description = """
                    ## 獲取指定角色的所有權限
                    
                    ### 功能說明
                    - 返回角色當前擁有的所有有效權限
                    - 只返回權限代碼列表
                    - 可用於權限檢查和 UI 控制
                    """)
    @GetMapping("/{id}/permissions")
    public ApiResponse<List<String>> getRolePermissions(
            @Parameter(description = "角色 ID", required = true)
            @PathVariable String id) {
        List<String> permissions = roleService.getRolePermissions(id);
        return ApiResponse.success("權限獲取成功", permissions);
    }

    @Operation(
            summary = "👥 獲取角色用戶數量",
            description = """
                    ## 獲取使用指定角色的用戶數量
                    
                    ### 功能說明
                    - 統計當前有多少用戶使用此角色
                    - 只統計啟用狀態的用戶
                    - 用於角色使用情況分析
                    """)
    @GetMapping("/{id}/users/count")
    public ApiResponse<Long> getUserCountByRole(
            @Parameter(description = "角色 ID", required = true)
            @PathVariable String id) {
        long count = roleService.getUserCountByRole(id);
        return ApiResponse.success("統計成功", count);
    }
}