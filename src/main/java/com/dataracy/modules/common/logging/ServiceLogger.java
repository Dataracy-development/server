package com.dataracy.modules.common.logging;

import java.time.Duration;
import java.time.Instant;

public class ServiceLogger extends BaseLogger {

    /**
     * 서비스 작업의 시작을 기록하고 시작 시각을 반환합니다.
     *
     * @param useCase 수행할 서비스의 용도 또는 이름
     * @param message  서비스 작업 내용
     * @return        작업 시작 시각의 {@link Instant} 객체
     */
    public Instant logStart(String useCase, String message) {
        Instant start = Instant.now();
        info("[Service 시작] {} message={}", useCase, message);
        return start;
    }

    /**
     * 서비스 작업이 성공적으로 완료되었음을 기록하고, 소요 시간을 로그에 남깁니다.
     *
     * @param useCase 수행된 서비스의 용도 또는 이름
     * @param message 서비스 작업 내용
     * @param startTime 서비스 작업이 시작된 시각
     */
    public void logSuccess(String useCase, String message, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        info("[Service 완료] {} message={} duration={}ms", useCase, message, durationMs);
    }

    /**
     * 서비스 작업 중 발생한 경고를 지정된 컨텍스트와 사유와 함께 로그로 기록합니다.
     *
     * @param context 경고가 발생한 상황 또는 위치에 대한 설명
     * @param reason 경고의 원인 또는 사유
     */
    public void logWarning(String context, String reason) {
        warn("[Service 경고] {} - {}", context, reason);
    }

    /**
     * 서비스 작업 중 발생한 예외를 useCase와 메시지와 함께 에러 로그로 기록합니다.
     *
     * @param useCase 예외가 발생한 서비스의 유스케이스 식별자
     * @param message 예외와 관련된 추가 메시지
     * @param e       기록할 예외 객체
     */
    public void logException(String useCase, String message, Throwable e) {
        error(e, "[Service 예외] useCase={} message={}", useCase, message);
    }
}

