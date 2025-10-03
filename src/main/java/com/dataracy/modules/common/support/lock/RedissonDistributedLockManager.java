/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.support.lock;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;

/** Redisson 분산락을 실행하여 락을 잡을 경우 수행하고, 실패 시 재시도하며, 예외를 명확하게 처리한다. */
@Component
public class RedissonDistributedLockManager {
  private final RedissonClient redissonClient;

  /**
   * RedissonClient를 사용하여 분산 락 매니저를 생성합니다.
   *
   * @param redissonClient 분산 락 처리를 위한 Redisson 클라이언트 인스턴스
   */
  public RedissonDistributedLockManager(RedissonClient redissonClient) {
    this.redissonClient = redissonClient;
  }

  /**
   * 지정된 키에 대해 분산 락을 획득한 뒤, 성공적으로 락을 획득하면 주어진 작업을 실행하고 그 결과를 반환합니다.
   *
   * <p>락 획득을 위해 최대 waitTime(밀리초) 동안 대기하며, 락을 획득하면 leaseTime(밀리초) 동안 소유합니다. 락 획득에 실패할 경우 최대
   * retryCount만큼 재시도합니다. 모든 시도에 실패하면 LockAcquisitionException이 발생합니다.
   *
   * @param key 분산 락을 식별하는 고유 키
   * @param waitTime 락 획득을 위해 대기할 최대 시간(밀리초)
   * @param leaseTime 락을 소유할 임대 시간(밀리초)
   * @param retryCount 락 획득 재시도 횟수
   * @param action 락 획득 후 실행할 작업
   * @return 작업 실행 결과
   * @throws LockAcquisitionException 락 획득 실패 또는 예외 발생 시
   * @throws BusinessException 작업 실행 중 비즈니스 예외 발생 시
   * @throws CommonException 작업 실행 중 공통 예외 발생 시
   */
  public <T> T execute(
      String key, long waitTime, long leaseTime, int retryCount, Supplier<T> action) {
    Instant startTime = LoggerFactory.lock().logStart(key, "분산 락 작업 시작");
    RLock lock = redissonClient.getLock(key);
    LoggerFactory.lock()
        .logInfo("lock 객체 생성 완료 - key={} class={}", key, lock.getClass().getSimpleName());

    return attemptLockAcquisition(key, lock, waitTime, leaseTime, retryCount, action, startTime);
  }

  /** 락 획득을 시도하고 성공 시 작업을 실행합니다. */
  private <T> T attemptLockAcquisition(
      String key,
      RLock lock,
      long waitTime,
      long leaseTime,
      int retryCount,
      Supplier<T> action,
      Instant startTime) {
    int attempts = 0;

    while (attempts <= retryCount) {
      boolean acquired = acquireLock(key, lock, waitTime, leaseTime);

      if (acquired) {
        try {
          T result = action.get();
          LoggerFactory.lock().logSuccess(key, "락 기반 작업 성공", startTime);
          return result;
        } catch (Exception e) {
          LoggerFactory.lock().logException(key, "락 기반 작업 실행 중 예외 발생", e);
          throw e;
        } finally {
          // 락을 실제로 획득한 경우에만 해제
          if (lock.isHeldByCurrentThread()) {
            releaseLock(key, lock);
          }
        }
      }

      attempts++;
      LoggerFactory.lock().logFail(key, attempts);
      performBackoff(attempts);
    }

    LoggerFactory.lock().logRetryExceeded(key);
    throw new LockAcquisitionException("다른 사용자가 해당 자원에 접근 중입니다. 잠시 후 다시 시도해주세요.");
  }

  /** 락 획득만 담당합니다. (락 해제는 하지 않음) 락 해제는 호출하는 메서드에서 finally 블록을 통해 처리됩니다. */
  @SuppressWarnings("java:S2222") // 락 해제는 호출자에서 처리됨
  private boolean acquireLock(String key, RLock lock, long waitTime, long leaseTime) {
    try {
      boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
      LoggerFactory.lock().logInfo("tryLock 결과 - key={} acquired={}", key, acquired);
      return acquired;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LoggerFactory.lock().logException(key, "인터럽트 발생", e);
      throw new LockAcquisitionException("스레드 인터럽트로 인해 락 획득 실패", e);
    } catch (Exception e) {
      LoggerFactory.lock().logException(key, "락 획득 중 예외 발생", e);
      throw new LockAcquisitionException("분산 락 획득 중 예외 발생", e);
    }
  }

  private void performBackoff(int attempts) {
    // 지수 백오프: 100ms, 200ms, 400ms, 800ms...
    long backoffMs = Math.min(100L * (1L << (attempts - 1)), 5000L); // 최대 5초
    try {
      TimeUnit.MILLISECONDS.sleep(backoffMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new LockAcquisitionException("백오프 대기 중 인터럽트 발생", e);
    }
  }

  /**
   * 비동기로 분산 락을 획득하고 작업을 실행합니다.
   *
   * @param key 분산 락을 식별하는 고유 키
   * @param waitTime 락 획득을 위해 대기할 최대 시간(밀리초)
   * @param leaseTime 락을 소유할 임대 시간(밀리초)
   * @param retryCount 락 획득 재시도 횟수
   * @param action 락 획득 후 실행할 작업
   * @return CompletableFuture로 래핑된 작업 실행 결과
   */
  public <T> CompletableFuture<T> executeAsync(
      String key, long waitTime, long leaseTime, int retryCount, Supplier<T> action) {
    return CompletableFuture.supplyAsync(
        () -> execute(key, waitTime, leaseTime, retryCount, action));
  }

  /**
   * 현재 스레드가 보유한 분산 락을 해제합니다.
   *
   * @param key 해제할 락의 식별자
   * @param lock 해제할 Redisson 락 객체
   */
  private void releaseLock(String key, RLock lock) {
    try {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
        LoggerFactory.lock().logUnlock(key);
      }
    } catch (Exception e) {
      LoggerFactory.lock().logUnlockFail(key, e);
    }
  }
}
