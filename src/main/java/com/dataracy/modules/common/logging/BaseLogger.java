package com.dataracy.modules.common.logging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseLogger {

    protected void info(String format, Object... args) {
        log.info(format, args);
    }

    protected void debug(String format, Object... args) {
        log.debug(format, args);
    }

    protected void warn(String format, Object... args) {
        log.warn(format, args);
    }

    protected void error(String format, Object... args) {
        log.error(format, args);
    }

    protected void error(Throwable t, String format, Object... args) {
        log.error(format, args, t);
    }
}
