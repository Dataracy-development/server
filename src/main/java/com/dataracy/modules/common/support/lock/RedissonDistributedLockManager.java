package com.dataracy.modules.common.support.lock;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson 분산락을 실행하여 락을 잡을 경우 수행하고,
 * 실패 시 재시도하며, 예외를 명확하게 처리한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonDistributedLockManager {

    private final RedissonClient redissonClient;

    public <T> T execute(String key, long waitTime, long leaseTime, int retryCount, Supplier<T> action) {
        log.info("[LOCK DEBUG] 진입 확인 - key: {}", key);
        RLock lock = redissonClient.getLock(key);
        log.info("[LOCK DEBUG] lock 객체 생성 완료 - key: {}, class: {}", key, lock.getClass().getName());

        int attempts = 0;

        while (attempts <= retryCount) {
            try {
                log.debug("[LOCK] 락 획득 시도 - key: {}, attempt: {}", key, attempts);
                boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
                log.debug("[LOCK] tryLock 결과 - acquired: {}, key: {}", acquired, key);

                if (acquired) {
                    try {
                        return action.get(); // 여기서 예외 발생 가능
                    } finally {
                        releaseLock(key, lock);
                    }
                }

                attempts++;
                log.warn("[LOCK] 락 획득 실패 - key: {}, retry attempt: {}", key, attempts);
                Thread.sleep(100); // 간단한 재시도 대기

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("[LOCK] 인터럽트 발생 - key: {}", key, e);
                throw new LockAcquisitionException("스레드 인터럽트로 인해 락 획득 실패", e);

            } catch (BusinessException | CommonException e) {
                log.warn("[LOCK] 비즈니스 예외 발생 - key: {}, message: {}", key, e.getMessage());
                // 비즈니스 예외는 그대로 던짐
                throw e;
            } catch (RuntimeException e) {
                // action.get() 내부에서 발생한 비즈니스 예외가 RuntimeException으로 감싸져 들어온 경우 다시 풀어줌
                Throwable cause = e.getCause();
                if (cause instanceof BusinessException || cause instanceof CommonException) {
                    throw (RuntimeException) cause;
                }

                log.error("[LOCK] Runtime 예외 발생 - key: {}", key, e);
                throw new LockAcquisitionException("분산 락 실행 중 런타임 예외 발생", e);

            } catch (Exception e) {
                log.error("[LOCK] 시스템 예외 발생 - key: {}", key, e);
                throw new LockAcquisitionException("분산 락 실행 중 시스템 예외 발생", e);
            }
        }

        log.error("[LOCK] 재시도 초과로 락 획득 실패 - key: {}", key);
        throw new LockAcquisitionException("다른 사용자가 해당 자원에 접근 중입니다. 잠시 후 다시 시도해주세요.");
    }

    private void releaseLock(String key, RLock lock) {
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("[LOCK] 락 해제 성공 - key: {}", key);
            }
        } catch (Exception e) {
            log.error("[LOCK] 락 해제 실패 - key: {}", key, e);
        }
    }
}
