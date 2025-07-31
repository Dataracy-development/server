package com.dataracy.modules.common.logging;

public class DomainLogger extends BaseLogger {

    public void logRuleViolation(String ruleName, String message) {
        warn("[도메인 규칙 위반] rule={} reason={}", ruleName, message);
    }
}
