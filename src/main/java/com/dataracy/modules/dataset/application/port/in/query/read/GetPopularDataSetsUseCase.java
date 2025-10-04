package com.dataracy.modules.dataset.application.port.in.query.read;

import java.util.List;

import com.dataracy.modules.dataset.application.dto.response.read.PopularDataResponse;

public interface GetPopularDataSetsUseCase {
  /**
   * 지정한 개수만큼 인기 있는 데이터셋 목록을 반환합니다.
   *
   * @param size 반환할 인기 데이터셋의 최대 개수
   * @return 인기 데이터셋 정보를 담은 PopularDataResponse 객체 리스트
   */
  List<PopularDataResponse> getPopularDataSets(int size);
}
