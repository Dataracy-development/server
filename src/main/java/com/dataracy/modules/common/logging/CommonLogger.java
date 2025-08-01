package com.dataracy.modules.common.logging;

import com.dataracy.modules.common.logging.support.BaseLogger;

public class CommonLogger extends BaseLogger {
    /**
     * 지정된 주제와 메시지를 포함하여 오류 로그를 기록합니다.
     *
     * @param topic   오류와 관련된 주제
     * @param message 오류에 대한 상세 메시지
     */
    public void logError(String topic, String message) {
        error("[{} 오류] message={}", topic, message);
    }

    /**
     * 지정된 주제와 메시지, 예외 정보를 포함하여 오류 로그를 기록합니다.
     *
     * @param topic   오류가 발생한 주제 또는 영역
     * @param message 오류에 대한 상세 메시지
     * @param e       관련 예외 객체
     */
    public void logError(String topic, String message, Throwable e) {
        error(e, "[{} 오류] message={}", topic, message);
    }
}
