package com.dataracy.modules.common.logging;

public class PersistenceLogger extends BaseLogger {

    public void logSave(String entityName, String identifier, String message) {
        info("[DB 저장] {} - {} - {}", entityName, identifier, message);
    }

    public void logDelete(String entityName, String identifier, String message) {
        info("[DB 삭제] {} - {} - {}", entityName, identifier, message);
    }

    public void logUpdate(String entityName, String identifier, String message) {
        info("[DB 업데이트] {} - {} - {}", entityName, identifier, message);
    }

    public void logQuery(String entityName, String query, String message) {
        debug("[DB 조회] {} - {} - {}", entityName, query, message);
    }

    public void logError(String entityName, String message, Throwable e) {
        error(e, "[DB 오류] {} - {}", entityName, message);
    }
}
