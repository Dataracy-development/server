package com.dataracy.modules.project.application.port.in.query.search;

import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;

import java.util.List;

public interface SearchSimilarProjectsUseCase {
    /**
     * 지정된 프로젝트와 유사한 프로젝트 목록을 반환합니다.
     *
     * @param projectId 기준이 되는 프로젝트의 ID
     * @param size 반환할 유사 프로젝트의 최대 개수
     * @return 유사한 프로젝트 정보를 담은 ProjectSimilarSearchResponse 객체의 리스트
     */
    List<SimilarProjectResponse> searchSimilarProjects(Long projectId, int size);
}
