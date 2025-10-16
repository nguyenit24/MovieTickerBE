package com.example.MovieTicker.exception;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.MovieTicker.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setCode(400);
        apiResponse.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 400);
        body.put("message", ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> handleSQLIntegrity(SQLIntegrityConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 400);
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
    /**
     * Bắt lỗi validation cho tất cả các @RequestBody có annotation @Valid.
     * Phương thức này sẽ tự động được gọi khi validation thất bại.
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        // Tạo một Map để chứa danh sách các lỗi
        // Key: tên trường bị lỗi, Value: thông báo lỗi
        Map<String, String> errors = new HashMap<>();
        // Lặp qua tất cả các lỗi và đưa vào Map
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        // Lấy mã lỗi VALIDATION_ERROR đã tạo ở bước 1
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        // Xây dựng đối tượng ApiResponse để trả về cho frontend
        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(errors) // Đính kèm chi tiết lỗi của từng trường
                .build();
        // Trả về response với HTTP status 400 Bad Request
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
