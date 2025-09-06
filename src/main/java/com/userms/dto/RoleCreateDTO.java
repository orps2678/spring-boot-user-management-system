package com.userms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "角色創建請求")
public class RoleCreateDTO {
    
    @Schema(description = "角色名稱", example = "系統管理員", required = true)
    @NotBlank(message = "角色名稱不能為空")
    @Size(min = 2, max = 50, message = "角色名稱長度必須在 2-50 字元之間")
    private String roleName;
    
    @Schema(description = "角色代碼", example = "ADMIN", required = true)
    @NotBlank(message = "角色代碼不能為空")
    @Size(min = 2, max = 30, message = "角色代碼長度必須在 2-30 字元之間")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "角色代碼必須以大寫字母開頭，只能包含大寫字母、數字和底線")
    private String roleCode;
    
    @Schema(description = "角色描述", example = "擁有系統所有權限的管理員角色")
    @Size(max = 200, message = "角色描述長度不能超過 200 字元")
    private String description;
}