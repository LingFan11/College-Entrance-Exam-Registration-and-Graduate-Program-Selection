package com.lingfan.xspp.common;

public class ApiResponse<T> {
    private String status; // ok | unfit | error
    private T data;
    private String reason; // for unfit
    private String message; // for error
    private String traceId;

    public static <T> ApiResponse<T> ok(T data, String traceId) {
        ApiResponse<T> r = new ApiResponse<>();
        r.status = "ok";
        r.data = data;
        r.traceId = traceId;
        return r;
    }

    public static <T> ApiResponse<T> unfit(String reason, String traceId) {
        ApiResponse<T> r = new ApiResponse<>();
        r.status = "unfit";
        r.reason = reason;
        r.traceId = traceId;
        return r;
    }

    public static <T> ApiResponse<T> error(String message, String traceId) {
        ApiResponse<T> r = new ApiResponse<>();
        r.status = "error";
        r.message = message;
        r.traceId = traceId;
        return r;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
}
