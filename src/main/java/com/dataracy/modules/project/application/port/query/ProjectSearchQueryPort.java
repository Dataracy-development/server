package com.dataracy.modules.project.application.port.query;

import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;

import java.util.List;

public interface ProjectSearchQueryPort {
    /**
 * 주어진 키워드로 프로젝트를 검색하여 최대 지정된 개수만큼 결과를 반환합니다.
 *
 * @param keyword 검색에 사용할 키워드
 * @param size 반환할 최대 결과 개수
 * @return 검색된 프로젝트의 실시간 응답 객체 리스트
 */
List<ProjectRealTimeSearchResponse> search(String keyword, int size);
}
