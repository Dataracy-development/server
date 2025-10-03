/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.service.query;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.port.in.query.extractor.FindUserIdIncludingDeletedUseCase;
import com.dataracy.modules.dataset.application.port.in.query.extractor.FindUserIdUseCase;
import com.dataracy.modules.dataset.application.port.out.query.extractor.ExtractDataOwnerPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataExtractService implements FindUserIdUseCase, FindUserIdIncludingDeletedUseCase {
  private final ExtractDataOwnerPort extractDataOwnerPort;

  // Use Case 상수 정의
  private static final String FIND_USER_ID_USE_CASE = "FindUserIdUseCase";
  private static final String FIND_USER_ID_INCLUDING_DELETED_USE_CASE =
      "FindUserIdIncludingDeletedUseCase";

  /**
   * 주어진 데이터 ID에 해당하는 데이터셋 소유자의 사용자 ID를 반환합니다.
   *
   * @param dataId 소유자 ID를 조회할 데이터셋의 ID
   * @return 데이터셋 소유자의 사용자 ID
   */
  @Override
  @Transactional(readOnly = true)
  public Long findUserIdByDataId(Long dataId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(FIND_USER_ID_USE_CASE, "아이디를 통한 데이터셋 작성자 ID 조회 서비스 시작 dataId=" + dataId);
    Long userId = extractDataOwnerPort.findUserIdByDataId(dataId);
    LoggerFactory.service()
        .logSuccess(
            FIND_USER_ID_USE_CASE, "아이디를 통한 데이터셋 작성자 ID 조회 서비스 종료 dataId=" + dataId, startTime);
    return userId;
  }

  /**
   * 삭제된 데이터를 포함하여 주어진 데이터 ID에 해당하는 데이터셋 소유자의 사용자 ID를 반환합니다.
   *
   * @param dataId 조회할 데이터셋의 ID
   * @return 데이터셋 소유자의 사용자 ID
   */
  @Override
  @Transactional(readOnly = true)
  public Long findUserIdIncludingDeleted(Long dataId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                FIND_USER_ID_INCLUDING_DELETED_USE_CASE,
                "삭제된 데이터셋을 포함하여 아이디를 통한 데이터셋 작성자 ID 조회 서비스 시작 dataId=" + dataId);
    Long userId = extractDataOwnerPort.findUserIdIncludingDeleted(dataId);
    LoggerFactory.service()
        .logSuccess(
            FIND_USER_ID_INCLUDING_DELETED_USE_CASE,
            "삭제된 데이터셋을 포함하여 아이디를 통한 데이터셋 작성자 ID 조회 서비스 종료 dataId=" + dataId,
            startTime);
    return userId;
  }
}
