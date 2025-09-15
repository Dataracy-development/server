package com.dataracy.modules.common.logging.support;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseLogger {
    /**
     * 지정된 형식 문자열과 인자를 사용하여 정보 수준의 로그 메시지를 기록합니다.
     *
     * @param format 로그 메시지의 형식 문자열
     * @param args   형식 문자열에 적용할 인자들
     */
    protected void info(String format, Object... args) {
        log.info(format, args);
    }

    /**
     * 지정된 형식 문자열과 인자를 사용하여 디버그 레벨 로그 메시지를 기록합니다.
     *
     * @param format 로그 메시지의 형식 문자열
     * @param args   형식 문자열에 적용할 인자들
     */
    protected void debug(String format, Object... args) {
        log.debug(format, args);
    }

    /**
     * 지정된 형식 문자열과 인자를 사용하여 경고 메시지를 로그로 남깁니다.
     *
     * @param format 로그 메시지의 형식 문자열
     * @param args   형식 문자열에 적용할 인자들
     */
    protected void warn(String format, Object... args) {
        log.warn(format, args);
    }

    /**
     * 지정된 형식 문자열과 인자를 사용하여 에러 레벨 로그를 기록합니다.
     *
     * @param format 로그 메시지의 형식 문자열
     * @param args   형식 문자열에 적용할 인자들
     */
    protected void error(String format, Object... args) {
        log.error(format, args);
    }

    /**
     * 지정된 예외와 함께 포맷된 에러 메시지를 로그로 남깁니다.
     *
     * @param t      로그에 포함할 예외 객체
     * @param format 메시지 포맷 문자열
     * @param args   포맷 문자열에 적용할 인자들
     */
    protected void error(Throwable t, String format, Object... args) {
        log.error(format, args, t);
    }
}
