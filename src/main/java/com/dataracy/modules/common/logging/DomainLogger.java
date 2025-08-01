package com.dataracy.modules.common.logging;

import com.dataracy.modules.common.logging.support.BaseLogger;

public class DomainLogger extends BaseLogger {

    /**
     * 도메인 규칙 위반이 발생했을 때 경고 로그를 기록합니다.
     *
     * @param ruleName 위반된 도메인 규칙의 이름
     * @param message  위반 사유에 대한 설명
     */
    public void logRuleViolation(String ruleName, String message) {
        warn("[도메인 규칙 위반] rule={} reason={}", ruleName, message);
    }
}
