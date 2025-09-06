package com.userms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用戶登入請求")
public class UserLoginDTO {

    @Schema(description = "使用者名稱或電子郵件", 
            example = "john_doe", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "使用者名稱或電子郵件不能為空")
    private String usernameOrEmail;

    @Schema(description = "密碼", 
            example = "SecureP@ss123", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密碼不能為空")
    private String password;
}