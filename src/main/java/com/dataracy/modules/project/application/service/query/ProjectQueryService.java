package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.dto.response.ProjectSimilarSearchResponse;
import com.dataracy.modules.project.application.port.in.ProjectRealTimeSearchUseCase;
import com.dataracy.modules.project.application.port.in.ProjectSimilarRecommendationUseCase;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.application.port.query.ProjectRealTimeSearchPort;
import com.dataracy.modules.project.application.port.query.ProjectSimilarSearchPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectQueryService implements
        ProjectRealTimeSearchUseCase,
        ProjectSimilarRecommendationUseCase
{
    private final ProjectRepositoryPort projectRepositoryPort;
    private final ProjectRealTimeSearchPort projectRealTimeSearchPort;
    private final ProjectSimilarSearchPort projectSimilarSearchPort;
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
        return projectRealTimeSearchPort.search(keyword, size);
    }

    @Override
    public List<ProjectSimilarSearchResponse> findSimilarProjects(Long projectId, int size) {
        Project project = projectRepositoryPort.findProjectById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));

        return projectSimilarSearchPort.recommendSimilarProjects(project, size);
    }
}
