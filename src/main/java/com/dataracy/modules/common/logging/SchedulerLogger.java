package com.dataracy.modules.common.logging;

import com.dataracy.modules.common.logging.support.BaseLogger;

public class SchedulerLogger extends BaseLogger {

    /**
     * 스케줄러 작업이 시작됨을 정보 로그로 기록합니다.
     *
     * @param job 시작되는 스케줄러 작업의 이름 또는 식별자
     */
    public void logStart(String job) {
        info("[Scheduler 시작] job={}", job);
    }

    /**
     * 지정된 스케줄러 작업의 완료를 정보 로그로 기록합니다.
     *
     * @param job 완료된 스케줄러 작업의 이름
     */
    public void logComplete(String job) {
        info("[Scheduler 완료] job={}", job);
    }

    /**
     * 스케줄러 작업에서 발생한 예외를 로그로 기록합니다.
     *
     * @param job 예외가 발생한 스케줄러 작업의 이름
     * @param message 예외와 관련된 추가 메시지
     * @param e 발생한 예외 객체
     */
    public void logError(String job, String message, Throwable e) {
        error(e, "[Scheduler 예외] job={} message={}", job, message);
    }
}
