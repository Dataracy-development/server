package com.dataracy.modules.common.lock;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    String key(); // SpEL 표현식 ex.) "'lock:nickname:' + #dto.nickname"
    long waitTime() default 200L; // 락 대기 시간(ms)
    long leaseTime() default 3000L; // 자동 해제 시간(ms)
    int retry() default 3; // 락 획득 재시도 횟수
}
