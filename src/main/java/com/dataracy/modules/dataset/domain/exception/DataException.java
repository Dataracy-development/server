/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.domain.exception;

import com.dataracy.modules.common.exception.BusinessException;
import com.dataracy.modules.common.status.BaseErrorCode;

/**
 * 데이터셋 도메인 관련 비즈니스 예외
 *
 * <p>상속 구조: Object → Throwable → Exception → RuntimeException → CustomException → BusinessException
 * → DataException
 *
 * <p>BusinessException을 통해 비즈니스 예외와 일반 예외(CommonException)를 명확히 구분하며, 9개의 도메인 예외(Auth, User,
 * Project, Data, Comment, Like, Email, Reference, Security)가 동일한 계층 구조를 공유하여 일관된 예외 처리를 가능하게 합니다.
 */
@SuppressWarnings("squid:S110") // 상속 깊이는 의도된 아키텍처 설계
public class DataException extends BusinessException {
  /**
   * 데이터 도메인에서 발생한 비즈니스 예외를 지정된 에러 코드와 함께 생성합니다.
   *
   * @param errorCode 예외의 원인이 되는 에러 코드
   */
  public DataException(BaseErrorCode errorCode) {
    super(errorCode);
  }
}
