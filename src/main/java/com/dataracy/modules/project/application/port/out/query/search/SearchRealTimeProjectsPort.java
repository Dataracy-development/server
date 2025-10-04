package com.dataracy.modules.project.application.port.out.query.search;

import java.util.List;

import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;

public interface SearchRealTimeProjectsPort {
  /**
   * 주어진 키워드로 프로젝트를 실시간 검색하여 결과를 반환합니다.
   *
   * @param keyword 검색에 사용할 키워드
   * @param size 반환할 최대 결과 개수
   * @return 검색된 프로젝트의 실시간 검색 결과 목록
   */
  List<RealTimeProjectResponse> searchByKeyword(String keyword, int size);
}
