package com.dataracy.modules.project.application.port.in.query.search;

import java.util.List;

import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;

public interface SearchSimilarProjectsUseCase {
  /**
   * 지정된 프로젝트와 유사한 프로젝트의 목록을 조회합니다.
   *
   * @param projectId 유사도를 비교할 기준 프로젝트의 ID
   * @param size 반환할 유사 프로젝트의 최대 개수
   * @return 유사한 프로젝트 정보를 담은 SimilarProjectResponse 객체 리스트
   */
  List<SimilarProjectResponse> searchSimilarProjects(Long projectId, int size);
}
