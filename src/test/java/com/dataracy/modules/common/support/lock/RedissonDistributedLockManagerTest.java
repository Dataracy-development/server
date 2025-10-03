/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.common.support.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.exception.CommonException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RedissonDistributedLockManagerTest {

  @Mock private RedissonClient redissonClient;

  @Mock private RLock rLock;

  private RedissonDistributedLockManager lockManager;

  @BeforeEach
  void setUp() {
    lockManager = new RedissonDistributedLockManager(redissonClient);
    given(redissonClient.getLock(anyString())).willReturn(rLock);
  }

  @Nested
  @DisplayName("execute 메서드 테스트")
  class ExecuteTest {

    @Test
    @DisplayName("성공: 분산 락 획득 및 작업 실행")
    void execute_성공() throws Exception {
      // given
      String key = "test-lock";
      long waitTime = 1000L;
      long leaseTime = 5000L;
      int retryCount = 3;
      String expectedResult = "success";

      given(rLock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
      given(rLock.isHeldByCurrentThread()).willReturn(true);

      Supplier<String> action = () -> expectedResult;

      // when
      String result = lockManager.execute(key, waitTime, leaseTime, retryCount, action);

      // then
      assertThat(result).isEqualTo(expectedResult);
      then(rLock).should().tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
      then(rLock).should().unlock();
    }

    @Test
    @DisplayName("락 획득 실패 후 재시도 성공")
    void execute_락획득실패후재시도성공() throws Exception {
      // given
      String key = "test-lock";
      long waitTime = 1000L;
      long leaseTime = 5000L;
      int retryCount = 2;
      String expectedResult = "success";

      // 첫 번째 시도는 실패, 두 번째 시도는 성공
      given(rLock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS))
          .willReturn(false) // 첫 번째 시도 실패
          .willReturn(true); // 두 번째 시도 성공
      given(rLock.isHeldByCurrentThread()).willReturn(true);

      Supplier<String> action = () -> expectedResult;

      // when
      String result = lockManager.execute(key, waitTime, leaseTime, retryCount, action);

      // then
      assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("모든 재시도 실패 시 LockAcquisitionException 발생")
    void execute_모든재시도실패_LockAcquisitionException발생() throws Exception {
      // given
      String key = "test-lock";
      long waitTime = 1000L;
      long leaseTime = 5000L;
      int retryCount = 2;

      given(rLock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(false);

      Supplier<String> action = () -> "success";

      // when & then
      LockAcquisitionException exception =
          catchThrowableOfType(
              () -> lockManager.execute(key, waitTime, leaseTime, retryCount, action),
              LockAcquisitionException.class);
      assertAll(
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .isInstanceOf(LockAcquisitionException.class),
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .hasMessage("다른 사용자가 해당 자원에 접근 중입니다. 잠시 후 다시 시도해주세요."));
    }

    @Test
    @DisplayName("InterruptedException 발생 시 LockAcquisitionException 변환")
    void execute_InterruptedException_LockAcquisitionException변환() throws Exception {
      // given
      String key = "test-lock";
      long waitTime = 1000L;
      long leaseTime = 5000L;
      int retryCount = 1;

      given(rLock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS))
          .willThrow(new InterruptedException("Thread interrupted"));

      Supplier<String> action = () -> "success";

      // when & then
      LockAcquisitionException exception =
          catchThrowableOfType(
              () -> lockManager.execute(key, waitTime, leaseTime, retryCount, action),
              LockAcquisitionException.class);
      assertAll(
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .isInstanceOf(LockAcquisitionException.class),
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .hasMessage("스레드 인터럽트로 인해 락 획득 실패"));
    }

    @Test
    @DisplayName("BusinessException 발생 시 그대로 전파")
    void execute_BusinessException_그대로전파() throws Exception {
      // given
      String key = "test-lock";
      long waitTime = 1000L;
      long leaseTime = 5000L;
      int retryCount = 1;

      given(rLock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
      given(rLock.isHeldByCurrentThread()).willReturn(true);

      BusinessException businessException =
          new BusinessException(com.dataracy.modules.common.status.CommonErrorStatus.BAD_REQUEST) {
            // Anonymous implementation for testing
          };
      Supplier<String> action =
          () -> {
            throw businessException;
          };

      // when & then
      BusinessException exception =
          catchThrowableOfType(
              () -> lockManager.execute(key, waitTime, leaseTime, retryCount, action),
              BusinessException.class);
      assertAll(
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .isInstanceOf(BusinessException.class));
    }

    @Test
    @DisplayName("CommonException 발생 시 그대로 전파")
    void execute_CommonException_그대로전파() throws Exception {
      // given
      String key = "test-lock";
      long waitTime = 1000L;
      long leaseTime = 5000L;
      int retryCount = 1;

      given(rLock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
      given(rLock.isHeldByCurrentThread()).willReturn(true);

      CommonException commonException =
          new CommonException(
              com.dataracy.modules.common.status.CommonErrorStatus.DATA_ACCESS_EXCEPTION);
      Supplier<String> action =
          () -> {
            throw commonException;
          };

      // when & then
      CommonException exception =
          catchThrowableOfType(
              () -> lockManager.execute(key, waitTime, leaseTime, retryCount, action),
              CommonException.class);
      assertAll(
          () ->
              org.assertj.core.api.Assertions.assertThat(exception)
                  .isInstanceOf(CommonException.class));
    }
  }

  @Nested
  @DisplayName("executeAsync 메서드 테스트")
  class ExecuteAsyncTest {

    @Test
    @DisplayName("성공: 비동기 실행")
    void executeAsync_성공() throws Exception {
      // given
      String key = "test-lock";
      long waitTime = 1000L;
      long leaseTime = 5000L;
      int retryCount = 1;
      String expectedResult = "async-success";

      given(rLock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)).willReturn(true);
      given(rLock.isHeldByCurrentThread()).willReturn(true);

      Supplier<String> action = () -> expectedResult;

      // when
      CompletableFuture<String> future =
          lockManager.executeAsync(key, waitTime, leaseTime, retryCount, action);
      String result = future.join();

      // then
      assertThat(result).isEqualTo(expectedResult);
    }
  }
}
