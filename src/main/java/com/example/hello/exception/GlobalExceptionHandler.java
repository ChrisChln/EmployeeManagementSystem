package com.example.hello.exception;

import com.example.hello.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("System error:", e);
        return Result.error("System error: Contact the administrator");
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("File size exceeds the limit", e);
        return Result.error("File size exceeds the limit (max: 10MB)");
    }

    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public Result<Void> handleJwtException(io.jsonwebtoken.JwtException e) {
        log.error("JWT token error", e);
        return Result.error("Invalid token");
    }
} 