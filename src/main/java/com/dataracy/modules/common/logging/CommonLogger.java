package com.dataracy.modules.common.logging;

public class CommonLogger extends BaseLogger {
    public void logError(String topic, String message) {
        error("[{} 오류] message={}", topic, message);
    }

    public void logError(String topic, String message, Throwable e) {
        error(e, "[{} 오류] message={}", topic, message);
    }
}
