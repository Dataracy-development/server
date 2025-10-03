/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.port.in.query.extractor;

public interface FindUserIdIncludingDeletedUseCase {
  /**
   * 삭제된 데이터를 포함하여 지정된 데이터 ID에 연결된 사용자 ID를 조회합니다.
   *
   * @param dataId 사용자 ID를 조회할 데이터의 고유 식별자
   * @return 주어진 데이터 ID에 연결된 사용자 ID
   */
  Long findUserIdIncludingDeleted(Long dataId);
}
