package com.userms.controller;

import com.userms.common.ApiResponse;
import com.userms.common.PageResult;
import com.userms.dto.PermissionCreateDTO;
import com.userms.dto.PermissionDTO;
import com.userms.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "🔑 權限管理", description = "系統權限的 CRUD 操作和資源管理")
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "📋 獲取權限列表", description = "分頁獲取所有權限列表")
    @GetMapping
    public ApiResponse<PageResult<PermissionDTO>> getAllPermissions(
            @Parameter(description = "頁碼", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "resourceName") @RequestParam(defaultValue = "resourceName") String sortBy,
            @Parameter(description = "排序方向", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResult<PermissionDTO> result = permissionService.getAllPermissions(pageable);
        return ApiResponse.success("查詢成功", result);
    }

    @Operation(summary = "✅ 獲取啟用權限列表", description = "獲取所有啟用狀態的權限")
    @GetMapping("/active")
    public ApiResponse<PageResult<PermissionDTO>> getActivePermissions(
            @Parameter(description = "頁碼", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量", example = "10") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("resourceName", "actionType").ascending());
        PageResult<PermissionDTO> result = permissionService.getActivePermissions(pageable);
        return ApiResponse.success("查詢成功", result);
    }

    @Operation(summary = "🔍 根據 ID 獲取權限詳情", description = "獲取指定權限的詳細資訊")
    @GetMapping("/{id}")
    public ApiResponse<PermissionDTO> getPermissionById(@PathVariable String id) {
        PermissionDTO permission = permissionService.getPermissionById(id);
        return ApiResponse.success(permission);
    }

    @Operation(summary = "🔎 搜索權限", description = "根據關鍵字搜索權限")
    @GetMapping("/search")
    public ApiResponse<List<PermissionDTO>> searchPermissions(
            @Parameter(description = "搜索關鍵字", required = true) @RequestParam String keyword) {
        List<PermissionDTO> permissions = permissionService.searchPermissions(keyword);
        return ApiResponse.success("搜索成功", permissions);
    }

    @Operation(summary = "📁 根據資源獲取權限", description = "獲取指定資源的所有權限")
    @GetMapping("/resource/{resourceName}")
    public ApiResponse<List<PermissionDTO>> getPermissionsByResource(@PathVariable String resourceName) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByResource(resourceName);
        return ApiResponse.success("查詢成功", permissions);
    }

    @Operation(summary = "➕ 創建新權限", description = "創建新的系統權限")
    @PostMapping
    public ApiResponse<PermissionDTO> createPermission(@Valid @RequestBody PermissionCreateDTO permissionDTO) {
        PermissionDTO createdPermission = permissionService.createPermission(permissionDTO);
        return ApiResponse.success("權限創建成功", createdPermission);
    }

    @Operation(summary = "✏️ 更新權限資訊", description = "更新指定權限的基本資訊")
    @PutMapping("/{id}")
    public ApiResponse<PermissionDTO> updatePermission(@PathVariable String id, @Valid @RequestBody PermissionDTO permissionDTO) {
        PermissionDTO updatedPermission = permissionService.updatePermission(id, permissionDTO);
        return ApiResponse.success("權限更新成功", updatedPermission);
    }

    @Operation(summary = "✅ 啟用權限", description = "啟用指定權限")
    @PostMapping("/{id}/enable")
    public ApiResponse<Void> enablePermission(@PathVariable String id) {
        permissionService.enablePermission(id);
        return ApiResponse.success("權限啟用成功");
    }

    @Operation(summary = "❌ 停用權限", description = "停用指定權限")
    @PostMapping("/{id}/disable")
    public ApiResponse<Void> disablePermission(@PathVariable String id) {
        permissionService.disablePermission(id);
        return ApiResponse.success("權限停用成功");
    }

    @Operation(summary = "🗑️ 刪除權限", description = "永久刪除權限")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable String id) {
        permissionService.deletePermission(id);
        return ApiResponse.success("權限刪除成功");
    }

    @Operation(summary = "📂 獲取資源名稱列表", description = "獲取所有不重複的資源名稱")
    @GetMapping("/resources")
    public ApiResponse<List<String>> getResourceNames() {
        List<String> resources = permissionService.getDistinctResourceNames();
        return ApiResponse.success("查詢成功", resources);
    }

    @Operation(summary = "⚡ 獲取操作類型列表", description = "獲取所有不重複的操作類型")
    @GetMapping("/actions")
    public ApiResponse<List<String>> getActionTypes() {
        List<String> actions = permissionService.getDistinctActionTypes();
        return ApiResponse.success("查詢成功", actions);
    }
}