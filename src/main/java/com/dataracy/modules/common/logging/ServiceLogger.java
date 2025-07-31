package com.dataracy.modules.common.logging;

public class ServiceLogger extends BaseLogger {

    public void logStart(String useCase, String input) {
        info("[Service 시작] {} input={}", useCase, input);
    }

    public void logSuccess(String useCase, String result) {
        info("[Service 완료] {} result={}", useCase, result);
    }

    public void logWarning(String context, String reason) {
        warn("[Service 경고] {} - {}", context, reason);
    }

    public void logException(String useCase, String message, Throwable e) {
        error(e, "[Service 예외] useCase={} message={}", useCase, message);
    }
}

