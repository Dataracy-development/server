package com.dataracy.modules.common.logging;

import java.time.Duration;
import java.time.Instant;

public class ApiLogger extends BaseLogger {

    /**
     * API 요청에 대한 정보를 로그로 기록합니다.
     *
     * @param message 로그에 기록할 API 요청 메시지
     */
    public Instant logRequest(String message) {
        info("[API 요청] {}", message);
        return Instant.now();
    }

    /**
     * API 응답 메시지를 정보 로그로 기록합니다.
     *
     * @param message 응답에 대한 설명 또는 상세 메시지
     */
    public void logResponse(String message, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        info("[API 응답] {} duration={}ms", message, durationMs);
    }

    /**
     * API 유효성 검사 실패 시 경고 메시지를 기록합니다.
     *
     * @param message 유효성 실패에 대한 상세 설명
     */
    public void logValidation(String message) {
        warn("[API 유효성 실패] {}", message);
    }

    /**
     * API 예외 발생 시 오류 메시지와 예외 정보를 로그로 기록합니다.
     *
     * @param message 예외와 관련된 설명 메시지
     * @param e 발생한 예외 객체
     */
    public void logException(String message, Throwable e) {
        error(e, "[API 예외 발생] {}", message);
    }
}
