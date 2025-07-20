package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.request.ProjectFilterRequest;
import com.dataracy.modules.project.application.dto.response.*;
import com.dataracy.modules.project.application.mapper.FilterProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.PopularProjectsDtoMapper;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectRealTimeSearchPort;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectSimilarSearchPort;
import com.dataracy.modules.project.application.port.in.*;
import com.dataracy.modules.project.application.port.query.ProjectQueryRepositoryPort;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectQueryService implements
        ProjectRealTimeSearchUseCase,
        ProjectSimilarSearchUseCase,
        ProjectPopularSearchUseCase,
        ProjectFilteredSearchUseCase,
        ProjectDetailUseCase
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
    private final FilterProjectDtoMapper filterProjectDtoMapper;

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
     * 지정한 개수만큼 인기 프로젝트를 조회하여, 각 프로젝트에 사용자명과 주제, 분석 목적, 데이터 소스, 저자 레벨 등 다양한 라벨 정보를 포함한 응답 리스트를 반환합니다.
     *
     * @param size 조회할 인기 프로젝트의 최대 개수
     * @return 사용자명과 다양한 라벨 정보가 포함된 인기 프로젝트 응답 리스트
     */

    @Override
    @Transactional(readOnly = true)
    public List<ProjectPopularSearchResponse> findPopularProjects(int size) {
        List<Project> savedProjects = projectQueryRepositoryPort.findPopularProjects(size);

        LabelMappingResponse labelResponse = labelMapping(savedProjects);

        return savedProjects.stream()
                .map(project -> popularProjectsDtoMapper.toResponseDto(
                        project,
                        labelResponse.usernameMap().get(project.getUserId()),
                        labelResponse.topicLabelMap().get(project.getTopicId()),
                        labelResponse.analysisPurposeLabelMap().get(project.getAnalysisPurposeId()),
                        labelResponse.dataSourceLabelMap().get(project.getDataSourceId()),
                        labelResponse.authorLevelLabelMap().get(project.getAuthorLevelId())
                ))
                .toList();
    }

    /**
     * 필터 조건과 페이지 정보를 기반으로 프로젝트 목록을 조회하고, 각 프로젝트에 사용자명 및 다양한 라벨 정보를 매핑하여 반환합니다.
     *
     * @param request 프로젝트 필터링 조건을 담은 요청 객체
     * @param pageable 페이지네이션 및 정렬 정보를 담은 객체
     * @return 필터링된 프로젝트 응답 DTO의 페이지 객체
     */
    @Override
    public Page<ProjectFilterResponse> findFilterdProjects(ProjectFilterRequest request, Pageable pageable) {
        ProjectSortType sortType = (request.sortType() != null && !request.sortType().isEmpty())
                ? ProjectSortType.of(request.sortType())
                : null;
        Page<Project> savedProjects = projectQueryRepositoryPort.searchByFilters(request, pageable, sortType);

        LabelMappingResponse labelResponse = labelMapping(savedProjects.getContent());
        return savedProjects.map(project -> {
            // 자식 프로젝트들의 userId 수집
            List<Long> childUserIds = project.getChildProjects().stream()
                    .map(Project::getUserId)
                    .distinct()
                    .toList();

            // userId → username 일괄 조회
            Map<Long, String> childUsernames = findUsernameUseCase.findUsernamesByIds(childUserIds);

            return filterProjectDtoMapper.toResponseDto(
                    project,
                    labelResponse.usernameMap().get(project.getUserId()),
                    labelResponse.topicLabelMap().get(project.getTopicId()),
                    labelResponse.analysisPurposeLabelMap().get(project.getAnalysisPurposeId()),
                    labelResponse.dataSourceLabelMap().get(project.getDataSourceId()),
                    labelResponse.authorLevelLabelMap().get(project.getAuthorLevelId()),
                    childUsernames
            );
        });
    }

    /**
     * 주어진 프로젝트 컬렉션에서 사용자, 토픽, 분석 목적, 데이터 소스, 저자 레벨의 ID를 추출하여 각 ID에 대한 레이블 및 사용자명을 일괄 조회한 결과를 반환합니다.
     *
     * @param savedProjects 레이블 및 사용자명 매핑을 위한 프로젝트 컬렉션
     * @return 각 ID에 대한 사용자명 및 레이블 매핑 정보를 담은 LabelMappingResponse 객체
     */
    private LabelMappingResponse labelMapping(Collection<Project> savedProjects) {
        List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
        List<Long> analysisPurposeIds = savedProjects.stream().map(Project::getAnalysisPurposeId).toList();
        List<Long> dataSourceIds = savedProjects.stream().map(Project::getDataSourceId).toList();
        List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

        return new LabelMappingResponse(
                findUsernameUseCase.findUsernamesByIds(userIds),
                getTopicLabelFromIdUseCase.getLabelsByIds(topicIds),
                getAnalysisPurposeLabelFromIdUseCase.getLabelsByIds(analysisPurposeIds),
                getDataSourceLabelFromIdUseCase.getLabelsByIds(dataSourceIds),
                getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds)
        );
    }

    @Override
    public ProjectDetailResponse getProjectDetailInfo(Long projectId) {
        Project project = projectQueryRepositoryPort.findProjectById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));

        boolean hasChild = projectQueryRepositoryPort.existsByParentProjectId(projectId);
        boolean hasData = projectQueryRepositoryPort.existsProjectDataByProjectId(projectId);

        return new ProjectDetailResponse(
                project.getId(),
                project.getTitle(),
                findUsernameUseCase.findUsernameById(project.getUserId()),
                getAuthorLevelLabelFromIdUseCase.getLabelById(project.getAuthorLevelId()),
                null,
                getTopicLabelFromIdUseCase.getLabelById(project.getTopicId()),
                getAnalysisPurposeLabelFromIdUseCase.getLabelById(project.getAnalysisPurposeId()),
                getDataSourceLabelFromIdUseCase.getLabelById(project.getDataSourceId()),
                project.getIsContinue(),
                project.getParentProjectId(),
                project.getContent(),
                project.getFileUrl(),
                project.getCreatedAt(),
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount(),
                hasChild,
                hasData
        );
    }
}
