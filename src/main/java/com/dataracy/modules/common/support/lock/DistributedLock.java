package com.dataracy.modules.common.support.lock;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    String key(); // 예: "'lock:user:' + #userId"

    long waitTime() default 200L;     // 락 획득 대기 시간 (ms)
    long leaseTime() default 3000L;   // 락 점유 시간 (ms), 이후 자동 해제
    int retry() default 3;// 락 획득 재시도 횟수
}
