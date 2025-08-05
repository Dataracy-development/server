package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.application.mapper.search.FilteredProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.PopularProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.query.extractor.FindProjectLabelMapUseCase;
import com.dataracy.modules.project.application.port.in.query.search.SearchFilteredProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.GetPopularProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.search.SearchRealTimeProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.search.SearchSimilarProjectsUseCase;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.application.port.out.query.search.SearchFilteredProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.GetPopularProjectsPort;
import com.dataracy.modules.project.application.port.out.query.search.SearchRealTimeProjectsPort;
import com.dataracy.modules.project.application.port.out.query.search.SearchSimilarProjectsPort;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.profile.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectSearchService implements
        SearchRealTimeProjectsUseCase,
        SearchSimilarProjectsUseCase,
        SearchFilteredProjectsUseCase
{
    private final FilteredProjectDtoMapper filteredProjectDtoMapper;

    private final FindProjectPort findProjectPort;

    private final SearchRealTimeProjectsPort searchRealTimeProjectsPort;
    private final SearchSimilarProjectsPort searchSimilarProjectsPort;
    private final SearchFilteredProjectsPort searchFilteredProjectsPort;

    private final FindUsernameUseCase findUsernameUseCase;
    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final FindProjectLabelMapUseCase findProjectLabelMapUseCase;

    /**
     * 주어진 키워드로 실시간 프로젝트를 검색하여 결과 목록을 반환합니다.
     *
     * 키워드가 null이거나 공백만 포함된 경우 빈 리스트를 반환합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 최대 결과 개수
     * @return 키워드에 매칭되는 실시간 프로젝트 응답 객체 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<RealTimeProjectResponse> searchByKeyword(String keyword, int size) {
        Instant startTime = LoggerFactory.service().logStart("SearchRealTimeProjectsUseCase", "자동완성을 위한 실시간 프로젝트 목록 조회 서비스 시작 keyword=" + keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        List<RealTimeProjectResponse> realTimeProjectResponses = searchRealTimeProjectsPort.searchByKeyword(keyword, size);
        LoggerFactory.service().logSuccess("SearchRealTimeProjectsUseCase", "자동완성을 위한 실시간 프로젝트 목록 조회 서비스 종료 keyword=" + keyword, startTime);
        return realTimeProjectResponses;
    }

    /**
     * 기준 프로젝트와 유사한 프로젝트 목록을 조회합니다.
     *
     * @param projectId 유사도를 비교할 기준 프로젝트의 ID
     * @param size 반환할 유사 프로젝트의 최대 개수
     * @return 유사한 프로젝트 응답 객체 리스트
     * @throws ProjectException 기준 프로젝트가 존재하지 않을 경우 발생합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public List<SimilarProjectResponse> searchSimilarProjects(Long projectId, int size) {
        Instant startTime = LoggerFactory.service().logStart("SearchSimilarProjectsUseCase", "유사 프로젝트 목록 조회 서비스 시작 projectId=" + projectId);
        Project project = findProjectPort.findProjectById(projectId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("SearchSimilarProjectsUseCase", "해당 프로젝트가 존재하지 않습니다. projectId=" + projectId);
                    return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });
        List<SimilarProjectResponse> similarProjectResponses = searchSimilarProjectsPort.searchSimilarProjects(project, size);
        LoggerFactory.service().logSuccess("SearchSimilarProjectsUseCase", "유사 프로젝트 목록 조회 서비스 종료 projectId=" + projectId, startTime);
        return similarProjectResponses;
    }

    /**
     * 필터 조건과 페이지 정보를 기반으로 프로젝트 목록을 조회하고, 각 프로젝트에 사용자명 및 다양한 라벨 정보를 매핑하여 페이지 형태로 반환합니다.
     *
     * @param request 프로젝트 필터링 조건이 포함된 요청 객체
     * @param pageable 페이지네이션 및 정렬 정보
     * @return 필터링된 프로젝트 응답 DTO의 페이지 객체
     */
    @Override
    public Page<FilteredProjectResponse> searchByFilters(FilteringProjectRequest request, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("SearchFilteredProjectsUseCase", "필터링된 프로젝트 목록 조회 서비스 시작 keyword=" + request.keyword());

        ProjectSortType sortType = (request.sortType() != null && !request.sortType().isEmpty())
                ? ProjectSortType.of(request.sortType())
                : null;
        Page<Project> savedProjects = searchFilteredProjectsPort.searchByFilters(request, pageable, sortType);

        ProjectLabelMapResponse labelResponse = findProjectLabelMapUseCase.labelMapping(savedProjects.getContent());
        Page<FilteredProjectResponse> filteredProjectResponses = savedProjects.map(project -> {
            // 자식 프로젝트들의 userId 수집
            List<Long> childUserIds = project.getChildProjects().stream()
                    .map(Project::getUserId)
                    .distinct()
                    .toList();

            // userId → username 일괄 조회
            Map<Long, String> childUsernames = findUsernameUseCase.findUsernamesByIds(childUserIds);

            return filteredProjectDtoMapper.toResponseDto(
                    project,
                    labelResponse.usernameMap().get(project.getUserId()),
                    labelResponse.topicLabelMap().get(project.getTopicId()),
                    labelResponse.analysisPurposeLabelMap().get(project.getAnalysisPurposeId()),
                    labelResponse.dataSourceLabelMap().get(project.getDataSourceId()),
                    labelResponse.authorLevelLabelMap().get(project.getAuthorLevelId()),
                    childUsernames
            );
        });

        LoggerFactory.service().logSuccess("SearchFilteredProjectsUseCase", "인기 프로젝트 목록 조회 서비스 종료 keyword=" + request.keyword(), startTime);
        return filteredProjectResponses;
    }
}
