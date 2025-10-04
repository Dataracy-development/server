package com.dataracy.modules.dataset.application.port.in.query.read;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dataracy.modules.dataset.application.dto.response.read.ConnectedDataResponse;

public interface FindConnectedDataSetsUseCase {
  /**
   * 특정 프로젝트에 연결된 데이터셋을 페이지 단위로 조회합니다.
   *
   * @param projectId 연결된 데이터셋을 조회할 프로젝트의 식별자
   * @param pageable 페이지네이션 및 정렬 정보를 포함하는 객체
   * @return 연결된 데이터셋 정보를 담은 페이지 객체
   */
  Page<ConnectedDataResponse> findConnectedDataSetsAssociatedWithProject(
      Long projectId, Pageable pageable);

  /**
   * 주어진 데이터셋 ID 목록에 해당하는 데이터셋 정보를 조회합니다.
   *
   * @param dataIds 조회할 데이터셋의 ID 목록
   * @return 요청한 ID에 해당하는 데이터셋 응답 객체 리스트
   */
  List<ConnectedDataResponse> findDataSetsByIds(List<Long> dataIds);
}
