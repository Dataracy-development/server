package com.dataracy.modules.common.support.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 분산 락을 적용하기 위한 어노테이션
 *
 * <p>Redisson을 사용하여 분산 환경에서 동시성 제어를 수행합니다.
 * SpEL 표현식을 사용하여 락 키를 동적으로 생성할 수 있습니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
  /**
   * 락 키 (SpEL 표현식 지원)
   *
   * <p>예: "'lock:signup:email:' + #requestDto.email()"
   */
  String key();

  /**
   * 락 획득 대기 시간 (밀리초)
   *
   * <p>락을 획득할 수 없을 때 대기하는 최대 시간
   */
  long waitTime() default 3000L;

  /**
   * 락 유지 시간 (밀리초)
   *
   * <p>락을 획득한 후 자동으로 해제되는 시간 (데드락 방지)
   */
  long leaseTime() default 5000L;

  /**
   * 락 획득 실패 시 재시도 횟수
   */
  int retry() default 1;
}

