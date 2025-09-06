package com.userms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用戶註冊請求")
public class UserRegisterDTO {

    @Schema(description = "使用者名稱", 
            example = "john_doe", 
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 3,
            maxLength = 50)
    @NotBlank(message = "使用者名稱不能為空")
    @Size(min = 3, max = 50, message = "使用者名稱長度必須在 3-50 字元之間")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "使用者名稱只能包含字母、數字和底線")
    private String username;

    @Schema(description = "電子郵件地址", 
            example = "john.doe@example.com", 
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100)
    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "電子郵件格式不正確")
    @Size(max = 100, message = "電子郵件長度不能超過 100 字元")
    private String email;

    @Schema(description = "密碼（必須包含大小寫字母、數字和特殊字元）", 
            example = "SecureP@ss123", 
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 8,
            maxLength = 100)
    @NotBlank(message = "密碼不能為空")
    @Size(min = 8, max = 100, message = "密碼長度必須在 8-100 字元之間")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "密碼必須包含大小寫字母、數字和特殊字元")
    private String password;

    @Schema(description = "確認密碼", 
            example = "SecureP@ss123", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "確認密碼不能為空")
    private String confirmPassword;

    @Schema(description = "名字", 
            example = "John", 
            maxLength = 50)
    @Size(max = 50, message = "名字長度不能超過 50 字元")
    private String firstName;

    @Schema(description = "姓氏", 
            example = "Doe", 
            maxLength = 50)
    @Size(max = 50, message = "姓氏長度不能超過 50 字元")
    private String lastName;
}