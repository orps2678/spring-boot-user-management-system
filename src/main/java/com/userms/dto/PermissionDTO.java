package com.userms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "權限資訊響應")
public class PermissionDTO {
    
    @Schema(description = "權限 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;
    
    @Schema(description = "權限名稱", example = "用戶讀取權限")
    @NotBlank(message = "權限名稱不能為空")
    @Size(min = 2, max = 100, message = "權限名稱長度必須在 2-100 字元之間")
    private String permissionName;
    
    @Schema(description = "權限代碼", example = "USER_READ")
    @NotBlank(message = "權限代碼不能為空")
    @Size(min = 2, max = 50, message = "權限代碼長度必須在 2-50 字元之間")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "權限代碼必須以大寫字母開頭，只能包含大寫字母、數字和底線")
    private String permissionCode;
    
    @Schema(description = "資源名稱", example = "User")
    @NotBlank(message = "資源名稱不能為空")
    @Size(min = 2, max = 100, message = "資源名稱長度必須在 2-100 字元之間")
    private String resourceName;
    
    @Schema(description = "操作類型", example = "READ")
    @NotBlank(message = "操作類型不能為空")
    @Size(min = 2, max = 50, message = "操作類型長度必須在 2-50 字元之間")
    private String actionType;
    
    @Schema(description = "啟用狀態", example = "true")
    private Boolean isActive;
    
    @Schema(description = "使用此權限的角色數量", example = "3")
    private Long roleCount;
    
    @Schema(description = "創建時間", example = "2024-01-01 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新時間", example = "2024-01-01 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
}