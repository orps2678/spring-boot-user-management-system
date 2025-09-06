package com.userms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "權限創建請求")
public class PermissionCreateDTO {
    
    @Schema(description = "權限名稱", example = "用戶讀取權限", required = true)
    @NotBlank(message = "權限名稱不能為空")
    @Size(min = 2, max = 100, message = "權限名稱長度必須在 2-100 字元之間")
    private String permissionName;
    
    @Schema(description = "權限代碼", example = "USER_READ", required = true)
    @NotBlank(message = "權限代碼不能為空")
    @Size(min = 2, max = 50, message = "權限代碼長度必須在 2-50 字元之間")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "權限代碼必須以大寫字母開頭，只能包含大寫字母、數字和底線")
    private String permissionCode;
    
    @Schema(description = "資源名稱", example = "User", required = true)
    @NotBlank(message = "資源名稱不能為空")
    @Size(min = 2, max = 100, message = "資源名稱長度必須在 2-100 字元之間")
    private String resourceName;
    
    @Schema(description = "操作類型", example = "READ", required = true)
    @NotBlank(message = "操作類型不能為空")
    @Size(min = 2, max = 50, message = "操作類型長度必須在 2-50 字元之間")
    private String actionType;
}