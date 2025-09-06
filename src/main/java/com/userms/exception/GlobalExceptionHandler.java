package com.userms.exception;

import com.userms.common.ApiResponse;
import com.userms.common.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        log.warn("業務異常: {}, 錯誤代碼: {}", e.getMessage(), e.getErrorCode());
        ApiResponse<Object> response = ApiResponse.error(e.getMessage(), e.getErrorCode(), e.getData());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        
        log.warn("參數驗證失敗: {}", errors);
        ApiResponse<Map<String, String>> response = ApiResponse.error("參數驗證失敗", ErrorCodes.VALIDATION_ERROR, errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        }
        
        log.warn("約束驗證失敗: {}", errors);
        ApiResponse<Map<String, String>> response = ApiResponse.error("參數驗證失敗", "CONSTRAINT_VIOLATION", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("請求體解析失敗: {}", e.getMessage());
        ApiResponse<Object> response = ApiResponse.error("請求格式錯誤", ErrorCodes.INVALID_REQUEST_FORMAT);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("參數 '%s' 類型錯誤，期望類型為 %s", e.getName(), e.getRequiredType().getSimpleName());
        log.warn("參數類型錯誤: {}", message);
        ApiResponse<Object> response = ApiResponse.error(message, ErrorCodes.INVALID_PARAMETER_TYPE);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("數據完整性違反: {}", e.getMessage());
        String message = "數據操作失敗，可能存在重複或約束違反";
        if (e.getMessage() != null && e.getMessage().contains("unique")) {
            message = "數據已存在，不能重複";
        }
        ApiResponse<Object> response = ApiResponse.error(message, "DATA_INTEGRITY_VIOLATION");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("資源未找到: {}", e.getResourcePath());
        ApiResponse<Object> response = ApiResponse.error("請求的資源不存在", "RESOURCE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法參數: {}", e.getMessage());
        ApiResponse<Object> response = ApiResponse.error(e.getMessage(), "ILLEGAL_ARGUMENT");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e) {
        log.error("運行時異常: {}", e.getMessage(), e);
        ApiResponse<Object> response = ApiResponse.error("系統內部錯誤", "RUNTIME_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception e) {
        log.error("未預期的異常: {}", e.getMessage(), e);
        ApiResponse<Object> response = ApiResponse.error("系統發生未知錯誤", "UNKNOWN_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}