package com.dataracy.modules.common.logging;

import com.dataracy.modules.common.logging.support.BaseLogger;

import java.time.Duration;
import java.time.Instant;

public class QueryDslLogger extends BaseLogger {
    /**
     * 데이터베이스 조회 작업의 시작을 로그로 기록하고 현재 시각을 반환합니다.
     *
     * @param entityName   조회 대상 엔티티의 이름
     * @param queryContext 조회 작업의 컨텍스트 또는 설명
     * @return             조회 시작 시점의 {@link Instant}
     */
    public Instant logQueryStart(String entityName, String queryContext) {
        debug("[QueryDsl 조회 시작] entity={} context={}", entityName, queryContext);
        return Instant.now();
    }

    /**
     * 데이터베이스 조회 작업이 완료되었음을 로그로 기록합니다.
     *
     * @param entityName   조회 대상 엔티티의 이름
     * @param queryContext 조회 작업의 컨텍스트 정보
     * @param startTime    조회 작업이 시작된 시각
     */
    public void logQueryEnd(String entityName, String queryContext, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        debug("[QueryDsl 조회 완료] entity={} context={} duration={}ms", entityName, queryContext, durationMs);
    }

    /**
     * 데이터베이스 저장 작업을 정보 수준으로 로그에 기록합니다.
     *
     * @param entityName 저장 대상 엔터티의 이름
     * @param identifier 엔터티의 식별자
     * @param message    추가 메시지 또는 설명
     */
    public void logSave(String entityName, String identifier, String message) {
        info("[QueryDsl 저장] {} - {} - {}", entityName, identifier, message);
    }

    /**
     * 데이터베이스 엔터티 삭제 작업을 정보 수준으로 로그에 기록합니다.
     *
     * @param entityName 삭제되는 엔터티의 이름
     * @param identifier 엔터티의 식별자
     * @param message    추가 메시지 또는 설명
     */
    public void logDelete(String entityName, String identifier, String message) {
        info("[QueryDsl 삭제] {} - {} - {}", entityName, identifier, message);
    }

    /**
     * 데이터베이스 엔티티의 업데이트 작업을 정보 수준으로 로그에 기록합니다.
     *
     * @param entityName 업데이트된 엔티티의 이름
     * @param identifier 엔티티의 고유 식별자
     * @param message    업데이트와 관련된 추가 메시지 또는 설명
     */
    public void logUpdate(String entityName, String identifier, String message) {
        info("[QueryDsl 업데이트] {} - {} - {}", entityName, identifier, message);
    }

    /**
     * 데이터베이스 엔티티의 존재 여부 확인 작업을 정보 수준으로 로그에 기록한다.
     *
     * @param entityName 존재 여부를 확인하는 엔티티의 이름
     * @param message    관련 메시지 또는 설명
     */
    public void logExist(String entityName, String message) {
        info("[QueryDsl 존재 여부 확인] {} - {}", entityName, message);
    }

    /**
     * 데이터베이스 작업과 관련된 경고 메시지를 경고 레벨로 기록합니다.
     *
     * @param entityName 경고와 관련된 엔티티 이름
     * @param message 경고 메시지 내용
     */
    public void logWarning(String entityName, String message) {
        warn("[QueryDsl 경고] {} - {}", entityName, message);
    }

    /**
     * 데이터베이스 작업 중 발생한 오류를 엔터티 이름과 메시지, 예외 정보와 함께 에러 레벨로 기록합니다.
     *
     * @param entityName 오류가 발생한 엔터티의 이름
     * @param message    오류에 대한 설명 메시지
     * @param e          발생한 예외 객체
     */
    public void logError(String entityName, String message, Throwable e) {
        error(e, "[QueryDsl 오류] {} - {}", entityName, message);
    }
}
