package com.dataracy.modules.project.application.port.in.query.search;

import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;

import java.util.List;

public interface SearchRealTimeProjectsUseCase {
    /**
     * 키워드를 기준으로 프로젝트를 실시간으로 검색하여 결과를 최대 지정 개수만큼 반환합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 최대 결과 개수
     * @return 검색된 프로젝트의 실시간 검색 응답 객체 리스트
     */
    List<RealTimeProjectResponse> searchByKeyword(String keyword, int size);
}
