package com.lingfan.xspp.common;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component("gradGlobalExceptionHandler")
@ControllerAdvice(basePackages = "com.lingfan.xspp.grad")
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String traceId = UUID.randomUUID().toString();
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ApiResponse.error(msg, traceId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ApiResponse<Void> handleConstraintViolation(ConstraintViolationException ex) {
        String traceId = UUID.randomUUID().toString();
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return ApiResponse.error(msg, traceId);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ApiResponse<Void> handleNotReadable(HttpMessageNotReadableException ex) {
        String traceId = UUID.randomUUID().toString();
        return ApiResponse.error("request_body_invalid", traceId);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        String traceId = UUID.randomUUID().toString();
        String code = ex.getCode();
        // Certain business codes are better represented as unfit (not error)
        if ("insufficient_profile".equals(code) || "no_match".equals(code)) {
            return ApiResponse.unfit(code, traceId);
        }
        return ApiResponse.error(code + ": " + ex.getMessage(), traceId);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResponse<Void> handleGeneric(Exception ex) {
        String traceId = UUID.randomUUID().toString();
        return ApiResponse.error("internal_error", traceId);
    }
}
