package com.dataracy.modules.common.logging;

public class ApiLogger extends BaseLogger {

    public void logRequest(String message) {
        info("[API 요청] {}", message);
    }

    public void logResponse(String message) {
        info("[API 응답] {}", message);
    }

    public void logValidation(String message) {
        warn("[API 유효성 실패] {}", message);
    }

    public void logException(String message, Throwable e) {
        error(e, "[API 예외 발생] {}", message);
    }
}
