package com.dataracy.modules.dataset.application.port.out.storage;

import java.util.List;
import java.util.Optional;

import com.dataracy.modules.dataset.application.dto.response.read.PopularDataResponse;

public interface PopularDataSetsStoragePort {
  /**
   * 인기 데이터셋 목록을 저장소에서 조회합니다.
   *
   * @return 저장된 인기 데이터셋 목록 (없으면 빈 Optional)
   */
  Optional<List<PopularDataResponse>> getPopularDataSets();

  /**
   * 인기 데이터셋 목록을 저장소에 저장합니다.
   *
   * @param popularDataSets 저장할 인기 데이터셋 목록
   */
  void setPopularDataSets(List<PopularDataResponse> popularDataSets);

  /**
   * 저장된 데이터의 마지막 업데이트 시간을 조회합니다.
   *
   * @return 마지막 업데이트 시간 (밀리초) 또는 빈 Optional
   */
  Optional<Long> getLastUpdateTime();

  /** 인기 데이터셋 저장소의 데이터를 삭제합니다. */
  void evictPopularDataSets();

  /**
   * 저장소에 유효한 데이터가 존재하는지 확인합니다.
   *
   * @return 유효한 데이터 존재 여부
   */
  boolean hasValidData();
}
