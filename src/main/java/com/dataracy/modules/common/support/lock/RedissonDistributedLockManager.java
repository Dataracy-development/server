package com.dataracy.modules.common.support.lock;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson 분산락을 실행하여 락을 잡을 경우 수행하고,
 * 실패 시 재시도하며, 예외를 명확하게 처리한다.
 */
@Component
public class RedissonDistributedLockManager {

    private final RedissonClient redissonClient;

    public RedissonDistributedLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 지정된 키에 대해 분산 락을 획득하고, 성공 시 주어진 작업을 실행한 후 결과를 반환합니다.
     *
     * @param key        분산 락을 식별하는 키
     * @param waitTime   락 획득을 위해 대기할 최대 시간(밀리초)
     * @param leaseTime  락을 소유할 임대 시간(밀리초)
     * @param retryCount 락 획득 재시도 횟수
     * @param action     락 획득 후 실행할 작업
     * @return 작업 실행 결과
     */
    public <T> T execute(String key, long waitTime, long leaseTime, int retryCount, Supplier<T> action) {
        Instant startTime = LoggerFactory.lock().logStart(key, "분산 락 작업 시작");
        RLock lock = redissonClient.getLock(key);
        LoggerFactory.lock().logInfo("lock 객체 생성 완료 - key={} class={}", key, lock.getClass().getSimpleName());

        int attempts = 0;

        while (attempts <= retryCount) {
            try {
                LoggerFactory.lock().logTry(key, attempts);
                boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
                LoggerFactory.lock().logInfo("tryLock 결과 - key={} acquired={}", key, acquired);

                if (acquired) {
                    try {
                        T result = action.get();
                        LoggerFactory.lock().logSuccess(key, "락 기반 작업 성공", startTime);
                        return result;
                    } finally {
                        releaseLock(key, lock);
                    }
                }

                attempts++;
                LoggerFactory.lock().logFail(key, attempts);
                Thread.sleep(100);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LoggerFactory.lock().logException(key, "인터럽트 발생", e);
                throw new LockAcquisitionException("스레드 인터럽트로 인해 락 획득 실패", e);

            } catch (BusinessException | CommonException e) {
                LoggerFactory.lock().logWarning(key, "비즈니스/공통 예외 발생 - message=" + e.getMessage());
                throw e;

            } catch (RuntimeException e) {
                Throwable cause = e.getCause();
                if (cause instanceof BusinessException || cause instanceof CommonException) {
                    throw (RuntimeException) cause;
                }
                LoggerFactory.lock().logException(key, "Runtime 예외 발생", e);
                throw new LockAcquisitionException("분산 락 실행 중 런타임 예외 발생", e);

            } catch (Exception e) {
                LoggerFactory.lock().logException(key, "시스템 예외 발생", e);
                throw new LockAcquisitionException("분산 락 실행 중 시스템 예외 발생", e);
            }
        }

        LoggerFactory.lock().logRetryExceeded(key);
        throw new LockAcquisitionException("다른 사용자가 해당 자원에 접근 중입니다. 잠시 후 다시 시도해주세요.");
    }

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
