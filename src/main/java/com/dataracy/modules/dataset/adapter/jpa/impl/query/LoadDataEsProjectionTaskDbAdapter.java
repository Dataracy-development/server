/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.jpa.impl.query;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.adapter.jpa.repository.DataEsProjectionTaskRepository;
import com.dataracy.modules.dataset.application.port.out.query.projection.LoadDataProjectionTaskPort;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoadDataEsProjectionTaskDbAdapter implements LoadDataProjectionTaskPort {
  private final DataEsProjectionTaskRepository repo;

  /**
   * 작업 처리용 프로젝션 태스크 배치를 조회한다.
   *
   * <p>지정한 시점(now)을 기준으로 주어진 상태 목록(statuses)에 해당하는 태스크를 페이지 단위로 조회하여 반환한다. 반환된 엔티티들은 작업 처리(worker)가
   * 처리할 배치로 사용된다.
   *
   * @param now 조회 기준 시점(주로 현재 시각)
   * @param statuses 조회할 태스크 상태 목록
   * @param pageable 페이징 및 정렬 정보
   * @return 조회된 DataEsProjectionTaskEntity 목록 (빈 리스트일 수 있음)
   */
  @Override
  public List<DataEsProjectionTaskEntity> findBatchForWork(
      LocalDateTime now, List<DataEsProjectionType> statuses, Pageable pageable) {
    return repo.findBatchForWork(now, statuses, pageable);
  }
}
