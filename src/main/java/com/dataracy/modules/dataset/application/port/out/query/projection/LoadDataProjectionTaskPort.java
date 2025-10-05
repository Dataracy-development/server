package com.dataracy.modules.dataset.application.port.out.query.projection;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.dataracy.modules.dataset.adapter.jpa.entity.DataEsProjectionTaskEntity;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;

public interface LoadDataProjectionTaskPort {
  /**
   * 배치 처리를 위해 작업 가능한 데이터 프로젝션 태스크를 조회한다.
   *
   * <p>지정한 기준 시각(now)과 상태 목록(statuses)에 맞는 태스크들을 pageable로 제한된 범위만큼 반환한다.
   *
   * @param now 조회 기준이 되는 현재 시각 — 이 시각을 기준으로 실행 가능 여부 등을 판단하는 용도로 사용된다.
   * @param statuses 포함할 프로젝션 타입(상태)들의 목록 — 이 목록에 포함된 타입들만 결과에 포함된다.
   * @param pageable 조회할 페이지 정보(페이지 번호, 크기, 정렬 등)
   * @return 조회 조건에 맞는 DataEsProjectionTaskEntity 목록 (페이지 단위로 제한됨)
   */
  List<DataEsProjectionTaskEntity> findBatchForWork(
      LocalDateTime now, List<DataEsProjectionType> statuses, Pageable pageable);
}
