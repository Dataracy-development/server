package com.dataracy.modules.common.support.lock;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.exception.CommonException;
import com.dataracy.modules.common.support.lock.LockAcquisitionException;
import com.dataracy.modules.common.logging.DistributedLockLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.status.CommonErrorStatus;
import com.dataracy.modules.dataset.domain.exception.DataException;
import com.dataracy.modules.dataset.domain.status.DataErrorStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedissonDistributedLockManagerTest {

    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RLock lock;

    private RedissonDistributedLockManager lockManager;
    private MockedStatic<LoggerFactory> loggerFactoryMock;
    private DistributedLockLogger lockLogger;

    @BeforeEach
    void setUp() {
        lockManager = new RedissonDistributedLockManager(redissonClient);
        loggerFactoryMock = mockStatic(LoggerFactory.class);
        lockLogger = mock(DistributedLockLogger.class);
        loggerFactoryMock.when(LoggerFactory::lock).thenReturn(lockLogger);
        
        // 기본 로거 설정
        lenient().when(lockLogger.logStart(anyString(), anyString())).thenReturn(Instant.now());
        lenient().doNothing().when(lockLogger).logInfo(anyString(), any());
        lenient().doNothing().when(lockLogger).logTry(anyString(), anyInt());
        lenient().doNothing().when(lockLogger).logFail(anyString(), anyInt());
        lenient().doNothing().when(lockLogger).logSuccess(anyString(), anyString(), any(Instant.class));
        lenient().doNothing().when(lockLogger).logWarning(anyString(), anyString());
        lenient().doNothing().when(lockLogger).logException(anyString(), anyString(), any(Exception.class));
        lenient().doNothing().when(lockLogger).logRetryExceeded(anyString());
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Nested
    @DisplayName("execute 메서드 테스트")
    class ExecuteTest {

        @Test
        @DisplayName("락 획득 성공 시 작업 실행")
        void executeSuccess() throws Exception {
            // given
            String key = "test-lock";
            long waitTime = 1000L;
            long leaseTime = 5000L;
            int retryCount = 3;
            String expectedResult = "success";

            Supplier<String> action = () -> expectedResult;

            given(redissonClient.getLock(key)).willReturn(lock);
            given(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
            given(lock.isHeldByCurrentThread()).willReturn(true);

            // when
            String result = lockManager.execute(key, waitTime, leaseTime, retryCount, action);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(redissonClient).should().getLock(key);
            then(lock).should().tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            then(lock).should().unlock();

            // 로깅 검증
            then(lockLogger).should().logStart(eq(key), eq("분산 락 작업 시작"));
            then(lockLogger).should().logInfo(eq("lock 객체 생성 완료 - key={} class={}"), eq(key), anyString());
            then(lockLogger).should().logTry(eq(key), eq(0));
            then(lockLogger).should().logInfo(eq("tryLock 결과 - key={} acquired={}"), eq(key), eq(true));
            then(lockLogger).should().logSuccess(eq(key), eq("락 기반 작업 성공"), any(Instant.class));
        }

        @Test
        @DisplayName("락 획득 실패 후 재시도 성공")
        void executeWithRetrySuccess() throws Exception {
            // given
            String key = "test-lock";
            long waitTime = 1000L;
            long leaseTime = 5000L;
            int retryCount = 2;
            String expectedResult = "success";

            Supplier<String> action = () -> expectedResult;

            given(redissonClient.getLock(key)).willReturn(lock);
            given(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS))
                    .willReturn(false)  // 첫 번째 시도 실패
                    .willReturn(true);  // 두 번째 시도 성공
            given(lock.isHeldByCurrentThread()).willReturn(true);

            // when
            String result = lockManager.execute(key, waitTime, leaseTime, retryCount, action);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(lock).should(times(2)).tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            then(lock).should().unlock();

            // 로깅 검증
            then(lockLogger).should().logTry(eq(key), eq(0));
            then(lockLogger).should().logTry(eq(key), eq(1));
            then(lockLogger).should().logFail(eq(key), eq(1));
        }

        @Test
        @DisplayName("최대 재시도 횟수 초과 시 LockAcquisitionException 발생")
        void executeRetryExceeded() throws Exception {
            // given
            String key = "test-lock";
            long waitTime = 1000L;
            long leaseTime = 5000L;
            int retryCount = 2;
            String expectedResult = "success";

            Supplier<String> action = () -> expectedResult;

            given(redissonClient.getLock(key)).willReturn(lock);
            given(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> lockManager.execute(key, waitTime, leaseTime, retryCount, action))
                    .isInstanceOf(LockAcquisitionException.class)
                    .hasMessage("다른 사용자가 해당 자원에 접근 중입니다. 잠시 후 다시 시도해주세요.");

            then(lock).should(times(3)).tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS); // 3번 시도 (0, 1, 2)
            then(lock).should(never()).unlock();

            // 로깅 검증
            then(lockLogger).should().logRetryExceeded(eq(key));
        }

        @Test
        @DisplayName("InterruptedException 발생 시 처리")
        void executeInterruptedException() throws Exception {
            // given
            String key = "test-lock";
            long waitTime = 1000L;
            long leaseTime = 5000L;
            int retryCount = 3;
            String expectedResult = "success";

            Supplier<String> action = () -> expectedResult;

            given(redissonClient.getLock(key)).willReturn(lock);
            given(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS))
                    .willThrow(new InterruptedException("Interrupted"));

            // when & then
            assertThatThrownBy(() -> lockManager.execute(key, waitTime, leaseTime, retryCount, action))
                    .isInstanceOf(LockAcquisitionException.class)
                    .hasMessage("스레드 인터럽트로 인해 락 획득 실패")
                    .hasCauseInstanceOf(InterruptedException.class);

            then(lockLogger).should().logException(eq(key), eq("인터럽트 발생"), any(InterruptedException.class));
        }

        @Test
        @DisplayName("BusinessException 발생 시 그대로 전파")
        void executeBusinessException() throws Exception {
            // given
            String key = "test-lock";
            long waitTime = 1000L;
            long leaseTime = 5000L;
            int retryCount = 3;
            DataException dataException = new DataException(DataErrorStatus.NOT_FOUND_DATA);

            Supplier<String> action = () -> {
                throw dataException;
            };

            given(redissonClient.getLock(key)).willReturn(lock);
            given(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
            given(lock.isHeldByCurrentThread()).willReturn(true);

            // when & then
            assertThatThrownBy(() -> lockManager.execute(key, waitTime, leaseTime, retryCount, action))
                    .isInstanceOf(DataException.class)
                    .isEqualTo(dataException);

            then(lock).should().unlock();
            then(lockLogger).should().logWarning(eq(key), contains("비즈니스/공통 예외 발생"));
        }

        @Test
        @DisplayName("CommonException 발생 시 그대로 전파")
        void executeCommonException() throws Exception {
            // given
            String key = "test-lock";
            long waitTime = 1000L;
            long leaseTime = 5000L;
            int retryCount = 3;
            CommonException commonException = new CommonException(CommonErrorStatus.INTERNAL_SERVER_ERROR);

            Supplier<String> action = () -> {
                throw commonException;
            };

            given(redissonClient.getLock(key)).willReturn(lock);
            given(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
            given(lock.isHeldByCurrentThread()).willReturn(true);

            // when & then
            assertThatThrownBy(() -> lockManager.execute(key, waitTime, leaseTime, retryCount, action))
                    .isInstanceOf(CommonException.class)
                    .isEqualTo(commonException);

            then(lock).should().unlock();
            then(lockLogger).should().logWarning(eq(key), contains("비즈니스/공통 예외 발생"));
        }

        @Test
        @DisplayName("RuntimeException with BusinessException cause 발생 시 처리")
        void executeRuntimeExceptionWithBusinessExceptionCause() throws Exception {
            // given
            String key = "test-lock";
            long waitTime = 1000L;
            long leaseTime = 5000L;
            int retryCount = 3;
            DataException dataException = new DataException(DataErrorStatus.NOT_FOUND_DATA);
            RuntimeException runtimeException = new RuntimeException("Runtime wrapper", dataException);

            Supplier<String> action = () -> {
                throw runtimeException;
            };

            given(redissonClient.getLock(key)).willReturn(lock);
            given(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
            given(lock.isHeldByCurrentThread()).willReturn(true);

            // when & then
            assertThatThrownBy(() -> lockManager.execute(key, waitTime, leaseTime, retryCount, action))
                    .isInstanceOf(DataException.class)
                    .isEqualTo(dataException);

            then(lock).should().unlock();
        }

        @Test
        @DisplayName("일반 RuntimeException 발생 시 LockAcquisitionException으로 래핑")
        void executeGeneralRuntimeException() throws Exception {
            // given
            String key = "test-lock";
            long waitTime = 1000L;
            long leaseTime = 5000L;
            int retryCount = 3;
            RuntimeException runtimeException = new RuntimeException("General runtime error");

            Supplier<String> action = () -> {
                throw runtimeException;
            };

            given(redissonClient.getLock(key)).willReturn(lock);
            given(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
            given(lock.isHeldByCurrentThread()).willReturn(true);

            // when & then
            assertThatThrownBy(() -> lockManager.execute(key, waitTime, leaseTime, retryCount, action))
                    .isInstanceOf(LockAcquisitionException.class)
                    .hasMessage("분산 락 실행 중 런타임 예외 발생")
                    .hasCause(runtimeException);

            then(lock).should().unlock();
            then(lockLogger).should().logException(eq(key), eq("Runtime 예외 발생"), eq(runtimeException));
        }

        @Test
        @DisplayName("일반 Exception 발생 시 LockAcquisitionException으로 래핑")
        void executeGeneralException() throws Exception {
            // given
            String key = "test-lock";
            long waitTime = 1000L;
            long leaseTime = 5000L;
            int retryCount = 3;
            Exception exception = new Exception("General error");

            Supplier<String> action = () -> {
                throw new RuntimeException(exception);
            };

            given(redissonClient.getLock(key)).willReturn(lock);
            given(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
            given(lock.isHeldByCurrentThread()).willReturn(true);

            // when & then
            assertThatThrownBy(() -> lockManager.execute(key, waitTime, leaseTime, retryCount, action))
                    .isInstanceOf(LockAcquisitionException.class)
                    .hasMessage("분산 락 실행 중 런타임 예외 발생")
                    .hasCauseInstanceOf(RuntimeException.class);

            then(lock).should().unlock();
            then(lockLogger).should().logException(eq(key), eq("Runtime 예외 발생"), any(RuntimeException.class));
        }
    }

    @Nested
    @DisplayName("executeAsync 메서드 테스트")
    class ExecuteAsyncTest {

        @Test
        @DisplayName("비동기 락 실행 성공")
        void executeAsyncSuccess() throws Exception {
            // given
            String key = "async-lock";
            long waitTime = 1000L;
            long leaseTime = 5000L;
            int retryCount = 3;
            String expectedResult = "async-success";

            Supplier<String> action = () -> expectedResult;

            given(redissonClient.getLock(key)).willReturn(lock);
            given(lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
            given(lock.isHeldByCurrentThread()).willReturn(true);

            // when
            CompletableFuture<String> future = lockManager.executeAsync(key, waitTime, leaseTime, retryCount, action);

            // then
            assertThat(future).isNotNull();
            assertThat(future.get()).isEqualTo(expectedResult);

            then(redissonClient).should().getLock(key);
            then(lock).should().tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            then(lock).should().unlock();
        }
    }
}
