package com.userms.common;

/**
 * 錯誤代碼常數定義
 */
public final class ErrorCodes {
    
    // 避免實例化
    private ErrorCodes() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // 用戶相關錯誤
    public static final String USERNAME_EXISTS = "USERNAME_EXISTS";
    public static final String EMAIL_EXISTS = "EMAIL_EXISTS";
    public static final String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String USER_INACTIVE = "USER_INACTIVE";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    
    // 請求相關錯誤
    public static final String INVALID_REQUEST_FORMAT = "INVALID_REQUEST_FORMAT";
    public static final String INVALID_PARAMETER_TYPE = "INVALID_PARAMETER_TYPE";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    
    // 角色相關錯誤
    public static final String ROLE_NOT_FOUND = "ROLE_NOT_FOUND";
    public static final String ROLE_ALREADY_ASSIGNED = "ROLE_ALREADY_ASSIGNED";
    public static final String ROLE_NOT_ASSIGNED = "ROLE_NOT_ASSIGNED";
    public static final String PERMISSION_NOT_FOUND = "PERMISSION_NOT_FOUND";
    
    // JWT 相關錯誤
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String TOKEN_MISSING = "TOKEN_MISSING";
}