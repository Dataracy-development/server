package com.dataracy.modules.common.logging;

import java.time.Duration;
import java.time.Instant;

public class PersistenceLogger extends BaseLogger {

    public Instant logQueryStart(String entityName, String queryContext) {
        debug("[DB 조회 시작] entity={} context={}", entityName, queryContext);
        return Instant.now();
    }

    public void logQueryEnd(String entityName, String queryContext, Instant startTime) {
        long durationMs = Duration.between(startTime, Instant.now()).toMillis();
        debug("[DB 조회 완료] entity={} context={} duration={}ms", entityName, queryContext, durationMs);
    }

    public void logSave(String entityName, String identifier, String message) {
        info("[DB 저장] {} - {} - {}", entityName, identifier, message);
    }

    public void logDelete(String entityName, String identifier, String message) {
        info("[DB 삭제] {} - {} - {}", entityName, identifier, message);
    }

    public void logUpdate(String entityName, String identifier, String message) {
        info("[DB 업데이트] {} - {} - {}", entityName, identifier, message);
    }

    public void logError(String entityName, String message, Throwable e) {
        error(e, "[DB 오류] {} - {}", entityName, message);
    }
}
