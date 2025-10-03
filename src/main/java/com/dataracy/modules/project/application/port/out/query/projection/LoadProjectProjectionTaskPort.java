/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.out.query.projection;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEsProjectionTaskEntity;
import com.dataracy.modules.project.domain.enums.ProjectEsProjectionType;

public interface LoadProjectProjectionTaskPort {
  /**
   * 지정한 시간과 상태 필터에 따라 처리할 투영(projection) 작업 엔티티의 배치를 조회한다.
   *
   * <p>주어진 기준 시각(now)보다 처리 대상인 작업을 상태(statuses)로 필터링하고, pageable에 따른 페이징/정렬 규칙을 적용해 결과 목록을 반환한다.
   *
   * @param now 조회 시점 기준으로 사용할 시간
   * @param statuses 포함할 투영 작업 상태 목록
   * @param pageable 페이징 및 정렬 정보
   * @return 조회된 ProjectEsProjectionTaskEntity 객체들의 리스트
   */
  List<ProjectEsProjectionTaskEntity> findBatchForWork(
      LocalDateTime now, List<ProjectEsProjectionType> statuses, Pageable pageable);
}
