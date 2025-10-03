/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.config.async;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 비동기 처리를 위한 설정 클래스 @EnableAsync 어노테이션을 통해 Spring의 비동기 처리 기능을 활성화하고, 커스텀 ThreadPoolTaskExecutor를
 * 설정하여 비동기 작업의 성능을 최적화합니다.
 *
 * <p>설정 내용: - Core Pool Size: 5 (기본 스레드 수) - Max Pool Size: 20 (최대 스레드 수) - Queue Capacity: 100 (대기
 * 큐 크기) - Thread Name Prefix: "async-" (스레드 이름 접두사) - Keep Alive Seconds: 60 (유휴 스레드 유지 시간)
 */
@Configuration
@EnableAsync
public class AsyncConfig {

  /**
   * 비동기 작업을 위한 커스텀 ThreadPoolTaskExecutor를 생성합니다.
   *
   * <p>이 Executor는 다음과 같은 특징을 가집니다: - 적절한 스레드 풀 크기로 리소스 효율성 확보 - 큐 기반 작업 관리로 메모리 효율성 향상 - 명확한 스레드
   * 이름으로 디버깅 용이성 제공
   *
   * @return 비동기 작업을 처리할 ThreadPoolTaskExecutor 인스턴스
   */
  @Bean(name = "asyncExecutor")
  public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    // 스레드 풀 설정
    executor.setCorePoolSize(5); // 기본 스레드 수
    executor.setMaxPoolSize(20); // 최대 스레드 수
    executor.setQueueCapacity(100); // 대기 큐 크기
    executor.setThreadNamePrefix("async-"); // 스레드 이름 접두사
    executor.setKeepAliveSeconds(60); // 유휴 스레드 유지 시간

    // 스레드 풀 종료 시 대기 중인 작업 완료 후 종료
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);

    // 스레드 풀 초기화
    executor.initialize();

    return executor;
  }
}
