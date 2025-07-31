package com.dataracy.modules.common.logging;

public class SchedulerLogger extends BaseLogger {

    public void logStart(String job) {
        info("[Scheduler 시작] job={}", job);
    }

    public void logComplete(String job) {
        info("[Scheduler 완료] job={}", job);
    }

    public void logError(String job, String message, Throwable e) {
        error(e, "[Scheduler 예외] job={}", message);
    }
}
