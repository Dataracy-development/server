/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.port.out.query.read;

import java.util.Optional;

import com.dataracy.modules.dataset.domain.model.Data;

public interface FindDataWithMetadataPort {
  /**
   * 주어진 ID에 해당하는 데이터와 관련 메타데이터를 조회합니다.
   *
   * @param dataId 조회할 데이터의 고유 식별자
   * @return 데이터와 메타데이터가 존재할 경우 해당 Data 객체를 포함한 Optional, 존재하지 않으면 빈 Optional
   */
  Optional<Data> findDataWithMetadataById(Long dataId);
}
