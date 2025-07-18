package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.port.in.ProjectRealTimeSearchUseCase;
import com.dataracy.modules.project.application.port.query.ProjectSearchQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectQueryService implements ProjectRealTimeSearchUseCase {
    private final ProjectSearchQueryPort projectSearchQueryPort;

    /**
     * 주어진 키워드로 실시간 프로젝트를 검색하여 결과 목록을 반환합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 최대 결과 개수
     * @return 검색된 프로젝트의 실시간 응답 객체 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectRealTimeSearchResponse> search(String keyword, int size) {
        return projectSearchQueryPort.search(keyword, size);
    }
}
