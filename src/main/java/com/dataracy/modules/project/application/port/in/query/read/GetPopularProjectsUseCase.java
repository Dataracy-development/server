package com.dataracy.modules.project.application.port.in.query.read;

import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;

import java.util.List;

public interface GetPopularProjectsUseCase {
    /**
     * 지정한 개수만큼 인기 프로젝트 목록을 조회합니다.
     *
     * @param size 반환할 인기 프로젝트의 최대 개수
     * @return 인기 프로젝트 정보를 담은 PopularProjectResponse 객체의 리스트
     */
    List<PopularProjectResponse> getPopularProjects(int size);
}
