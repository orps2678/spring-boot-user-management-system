package com.userms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "用戶資訊響應")
public class UserDTO {
    
    @Schema(description = "用戶 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;
    
    @Schema(description = "使用者名稱", example = "john_doe")
    private String username;
    
    @Schema(description = "電子郵件", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "名字", example = "John")
    private String firstName;
    
    @Schema(description = "姓氏", example = "Doe")
    private String lastName;
    
    @Schema(description = "完整姓名", example = "John Doe")
    private String fullName;
    
    @Schema(description = "帳戶狀態", example = "true")
    private Boolean isActive;
    
    @Schema(description = "用戶角色列表", example = "[\"USER\", \"ADMIN\"]")
    private List<String> roles;
    
    @Schema(description = "創建時間", example = "2024-01-01 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新時間", example = "2024-01-01 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
}