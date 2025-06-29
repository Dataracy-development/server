package com.dataracy.modules.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonDistributedLockManager {

    private final RedissonClient redissonClient;

    public <T> T execute(String key, long waitTime, long leaseTime, int retryCount, Supplier<T> action) {
        RLock lock = redissonClient.getLock(key);
        int attempts = 0;

        while (attempts <= retryCount) {
            try {
                log.debug("[LOCK] 락 획득 시도 - key: {}, attempt: {}", key, attempts);

                boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);

                if (acquired) {
                    log.debug("[LOCK] 락 획득 성공 - key: {}", key);
                    try {
                        return action.get();
                    } catch (Exception e) {
                        log.error("[LOCK] 락 내부 로직 실행 중 예외 - key: {}, message: {}", key, e.getMessage(), e);
                        throw new LockAcquisitionException("분산 락 실행 중 예외 발생", e);
                    } finally {
                        if (lock.isHeldByCurrentThread()) {
                            try {
                                lock.unlock();
                                log.debug("[LOCK] 락 해제 성공 - key: {}", key);
                            } catch (IllegalMonitorStateException e) {
                                log.error("[LOCK] 락 해제 실패 (다른 스레드 소유) - key: {}", key, e);
                            } catch (Exception e) {
                                log.error("[LOCK] 락 해제 중 알 수 없는 예외 - key: {}", key, e);
                            }
                        }
                    }
                } else {
                    log.warn("[LOCK] 락 획득 실패 - key: {}, attempt: {}", key, attempts);
                    attempts++;
                    Thread.sleep(100); // 재시도 간 딜레이
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("[LOCK] 락 획득 중 인터럽트 발생 - key: {}", key, e);
                throw new LockAcquisitionException("스레드 인터럽트로 인해 락 획득 실패", e);
            } catch (Exception e) {
                log.error("[LOCK] 락 획득 또는 실행 중 알 수 없는 예외 - key: {}", key, e);
                throw new LockAcquisitionException("분산 락 수행 중 알 수 없는 예외", e);
            }
        }

        log.error("[LOCK] 재시도 횟수 초과로 락 획득 실패 - key: {}", key);
        throw new LockAcquisitionException("락 획득 실패 (재시도 초과): " + key);
    }
}
