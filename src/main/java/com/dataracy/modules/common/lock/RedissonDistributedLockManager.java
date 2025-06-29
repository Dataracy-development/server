package com.dataracy.modules.common.lock;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RedissonDistributedLockManager {

    private final RedissonClient redissonClient;

    public <T> T execute(String key, long waitTime, long leaseTime, int retryCount, Supplier<T> action) {
        RLock lock = redissonClient.getLock(key);
        int attempts = 0;

        while (attempts <= retryCount) {
            try {
                if (lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)) {
                    try {
                        return action.get();
                    } finally {
                        if (lock.isHeldByCurrentThread()) {
                            lock.unlock();
                        }
                    }
                } else {
                    attempts++;
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new LockAcquisitionException("스레드 인터럽트로 인해 락 획득 실패");
            }
        }

        throw new LockAcquisitionException("락 획득 실패: " + key);
    }
}
