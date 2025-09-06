package com.userms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "認證響應")
public class AuthResponseDTO {
    
    private static final String DEFAULT_TOKEN_TYPE = "Bearer";
    
    @Schema(description = "JWT Token", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Token 類型", 
            example = DEFAULT_TOKEN_TYPE, 
            defaultValue = DEFAULT_TOKEN_TYPE)
    private String tokenType = DEFAULT_TOKEN_TYPE;
    
    @Schema(description = "用戶資訊")
    private UserDTO user;

    public AuthResponseDTO(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}