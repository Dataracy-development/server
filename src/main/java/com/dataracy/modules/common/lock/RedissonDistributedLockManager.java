package com.dataracy.modules.common.lock;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.CommonException;
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
        log.info("🟡 [LOCK DEBUG] 진입 확인 - key: {}", key);
        RLock lock = redissonClient.getLock(key);
        log.info("🟢 [LOCK DEBUG] lock 객체 생성 완료 - key: {}, class: {}", key, lock.getClass().getName());

        int attempts = 0;

        while (attempts <= retryCount) {
            try {
                log.debug("🔐 [LOCK] 락 획득 시도 - key: {}, attempt: {}", key, attempts);
                boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
                log.debug("✅ [LOCK] tryLock 결과 - acquired: {}, key: {}", acquired, key);

                if (acquired) {
                    try {
                        return action.get();
                    } finally {
                        releaseLock(key, lock);
                    }
                }

                attempts++;
                log.warn("⚠️ [LOCK] 락 획득 실패 - key: {}, retry attempt: {}", key, attempts);
                Thread.sleep(100); // 간단한 재시도 대기

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("❌ [LOCK] 인터럽트 발생 - key: {}", key, e);
                throw new LockAcquisitionException("스레드 인터럽트로 인해 락 획득 실패", e);

            } catch (BusinessException | CommonException e) {
                throw e;

            } catch (Exception e) {
                log.error("❌ [LOCK] 예외 발생 - key: {}", key, e);
                throw new LockAcquisitionException("분산 락 실행 중 예외 발생", e);
            }
        }

        log.error("⛔ [LOCK] 재시도 초과로 락 획득 실패 - key: {}", key);
        throw new LockAcquisitionException("다른 사용자가 해당 자원에 접근 중입니다. 잠시 후 다시 시도해주세요.");
    }

    private void releaseLock(String key, RLock lock) {
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("🔓 [LOCK] 락 해제 성공 - key: {}", key);
            }
        } catch (Exception e) {
            log.error("❌ [LOCK] 락 해제 실패 - key: {}", key, e);
        }
    }
}
