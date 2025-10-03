/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.jpa.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

public interface DataEsProjectionTaskRepository
    extends JpaRepository<DataEsProjectionTaskEntity, Long> {
  /**
   * 실행 대기 중인 작업을 페지미스틱 쓰기 락을 걸어 배치로 조회합니다.
   *
   * <p>조회된 행에는 PESSIMISTIC_WRITE 락이 적용되며 락이 걸린 행은 SKIP LOCKED 동작으로 건너뜁니다. 주어진 상태 목록에 속하고 `now` 시각
   * 이전 또는 같은 `nextRunAt`을 가진 엔티티를 `nextRunAt` 오름차순, 동일한 경우 `id` 오름차순으로 결정론적으로 정렬해 반환합니다. 결과는 전달된
   * Pageable에 따라 페이징(제한/오프셋 및 정렬 적용 가능)됩니다.
   *
   * @param now 조회 기준이 되는 현재 시각(이 시각 이전 또는 같은 nextRunAt을 가진 항목을 대상)
   * @param statuses 조회 대상이 될 상태 목록
   * @param pageable 페이징 및 추가 정렬 정보
   * @return 조회된 DataEsProjectionTaskEntity 목록 (페이지 크기만큼 제한됨)
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")) // SKIP LOCKED
  @Query(
      """
        select t
          from DataEsProjectionTaskEntity t
         where t.status in :statuses
           and t.nextRunAt <= :now
         order by t.nextRunAt ASC, t.id ASC
    """)
  List<DataEsProjectionTaskEntity> findBatchForWork(
      @Param("now") LocalDateTime now,
      @Param("statuses") List<DataEsProjectionType> statuses,
      Pageable pageable);
}
