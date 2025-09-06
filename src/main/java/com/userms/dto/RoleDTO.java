package com.userms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "角色資訊響應")
public class RoleDTO {
    
    @Schema(description = "角色 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;
    
    @Schema(description = "角色名稱", example = "系統管理員")
    @NotBlank(message = "角色名稱不能為空")
    @Size(min = 2, max = 50, message = "角色名稱長度必須在 2-50 字元之間")
    private String roleName;
    
    @Schema(description = "角色代碼", example = "ADMIN")
    @NotBlank(message = "角色代碼不能為空")
    @Size(min = 2, max = 30, message = "角色代碼長度必須在 2-30 字元之間")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "角色代碼必須以大寫字母開頭，只能包含大寫字母、數字和底線")
    private String roleCode;
    
    @Schema(description = "角色描述", example = "擁有系統所有權限的管理員角色")
    @Size(max = 200, message = "角色描述長度不能超過 200 字元")
    private String description;
    
    @Schema(description = "啟用狀態", example = "true")
    private Boolean isActive;
    
    @Schema(description = "權限列表", example = "[\"USER_READ\", \"USER_WRITE\"]")
    private List<String> permissions;
    
    @Schema(description = "用戶數量", example = "5")
    private Long userCount;
    
    @Schema(description = "創建時間", example = "2024-01-01 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新時間", example = "2024-01-01 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
}