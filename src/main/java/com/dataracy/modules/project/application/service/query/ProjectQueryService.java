package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.response.ProjectPopularSearchResponse;
import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.dto.response.ProjectSimilarSearchResponse;
import com.dataracy.modules.project.application.mapper.PopularProjectsDtoMapper;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectRealTimeSearchPort;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectSimilarSearchPort;
import com.dataracy.modules.project.application.port.in.ProjectPopularSearchUseCase;
import com.dataracy.modules.project.application.port.in.ProjectRealTimeSearchUseCase;
import com.dataracy.modules.project.application.port.in.ProjectSimilarSearchUseCase;
import com.dataracy.modules.project.application.port.query.ProjectQueryRepositoryPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectQueryService implements
        ProjectRealTimeSearchUseCase,
        ProjectSimilarSearchUseCase,
        ProjectPopularSearchUseCase
{
    private final PopularProjectsDtoMapper popularProjectsDtoMapper;

    private final ProjectRealTimeSearchPort projectRealTimeSearchPort;
    private final ProjectSimilarSearchPort projectSimilarSearchPort;
    private final ProjectQueryRepositoryPort projectQueryRepositoryPort;

    private final FindUsernameUseCase findUsernameUseCase;
    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    /**
     * 주어진 키워드로 실시간 프로젝트를 검색하여 결과 목록을 반환합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 최대 결과 개수
     * @return 키워드에 매칭되는 프로젝트의 실시간 검색 응답 객체 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectRealTimeSearchResponse> searchByKeyword(String keyword, int size) {
        return projectRealTimeSearchPort.search(keyword, size);
    }

    /**
     * 주어진 프로젝트와 유사한 프로젝트 목록을 조회합니다.
     *
     * @param projectId 기준이 되는 프로젝트의 ID
     * @param size 반환할 유사 프로젝트의 최대 개수
     * @return 유사한 프로젝트 응답 객체 리스트
     * @throws ProjectException 기준 프로젝트가 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectSimilarSearchResponse> findSimilarProjects(Long projectId, int size) {
        Project project = projectQueryRepositoryPort.findProjectById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));

        return projectSimilarSearchPort.recommendSimilarProjects(project, size);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<ProjectPopularSearchResponse> findPopularProjects(int size) {
//        List<Project> savedProjects = projectQueryRepositoryPort.findPopularProjects(size);
//        List<ProjectPopularSearchResponse> responseDto = savedProjects.stream()
//                .map(project -> {
//                    String username = findUsernameUseCase.findUsernameById(project.getUserId());
//                    String topicLabel = getTopicLabelFromIdUseCase.getLabelById(project.getTopicId());
//                    String analysisPurposeLabel = getAnalysisPurposeLabelFromIdUseCase.getLabelById(project.getAnalysisPurposeId());
//                    String dataSourceLabel = getDataSourceLabelFromIdUseCase.getLabelById(project.getDataSourceId());
//                    String authorLevelLabel = getAuthorLevelLabelFromIdUseCase.getLabelById(project.getAuthorLevelId());
//
//                    return popularProjectsDtoMapper.toResponseDto(
//                            project,
//                            username,
//                            topicLabel,
//                            analysisPurposeLabel,
//                            dataSourceLabel,
//                            authorLevelLabel
//                    );
//                })
//                .toList();
//        return responseDto;
/**
     * 지정한 개수만큼 인기 프로젝트 목록을 조회하고, 각 프로젝트에 관련된 사용자명 및 다양한 라벨 정보를 포함하여 반환합니다.
     *
     * @param size 반환할 인기 프로젝트의 최대 개수
     * @return 사용자명, 주제, 분석 목적, 데이터 소스, 저자 레벨 라벨이 포함된 인기 프로젝트 응답 리스트
     */

    @Override
    @Transactional(readOnly = true)
    public List<ProjectPopularSearchResponse> findPopularProjects(int size) {
        List<Project> savedProjects = projectQueryRepositoryPort.findPopularProjects(size);

        List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
        List<Long> analysisPurposeIds = savedProjects.stream().map(Project::getAnalysisPurposeId).toList();
        List<Long> dataSourceIds = savedProjects.stream().map(Project::getDataSourceId).toList();
        List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

        Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(userIds);
        Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);
        Map<Long, String> analysisPurposeLabelMap = getAnalysisPurposeLabelFromIdUseCase.getLabelsByIds(analysisPurposeIds);
        Map<Long, String> dataSourceLabelMap = getDataSourceLabelFromIdUseCase.getLabelsByIds(dataSourceIds);
        Map<Long, String> authorLevelLabelMap = getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);

        return savedProjects.stream()
                .map(project -> popularProjectsDtoMapper.toResponseDto(
                        project,
                        usernameMap.get(project.getUserId()),
                        topicLabelMap.get(project.getTopicId()),
                        analysisPurposeLabelMap.get(project.getAnalysisPurposeId()),
                        dataSourceLabelMap.get(project.getDataSourceId()),
                        authorLevelLabelMap.get(project.getAuthorLevelId())
                ))
                .toList();
    }
}
