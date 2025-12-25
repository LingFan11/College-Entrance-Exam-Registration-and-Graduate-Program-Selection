package com.lingfan.xspp.school.config;

import com.lingfan.xspp.common.ApiResponse;
import com.lingfan.xspp.common.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;
import java.util.stream.Collectors;

@Component("schoolExceptionHandler")
@ControllerAdvice(basePackages = "com.lingfan.xspp.school")
public class SchoolExceptionHandler {

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
        if ("not_found_or_incomplete".equals(code) || "school_not_found".equals(code)) {
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
