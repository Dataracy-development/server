package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.like.application.port.in.ValidateTargetLikeUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.project.adapter.redis.ProjectViewCountRedisAdapter;
import com.dataracy.modules.project.application.dto.request.ProjectFilterRequest;
import com.dataracy.modules.project.application.dto.response.*;
import com.dataracy.modules.project.application.mapper.ConnectedProjectAssociatedDtoMapper;
import com.dataracy.modules.project.application.mapper.ContinueProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.FilterProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.PopularProjectsDtoMapper;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectRealTimeSearchPort;
import com.dataracy.modules.project.application.port.elasticsearch.ProjectSimilarSearchPort;
import com.dataracy.modules.project.application.port.in.*;
import com.dataracy.modules.project.application.port.out.ProjectRepositoryPort;
import com.dataracy.modules.project.application.port.out.ProjectViewCountRedisPort;
import com.dataracy.modules.project.application.port.query.ProjectQueryRepositoryPort;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.model.vo.ProjectUser;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUsernameUseCase;
import com.dataracy.modules.user.application.port.in.user.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
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
        ProjectDetailUseCase,
        ContinueProjectUseCase,
        ConnectedProjectAssociatedWithDataUseCase,
        FindUserIdByProjectIdUseCase,
        FindUserIdIncludingDeletedProjectUseCase,
        ValidateProjectUseCase
{
    private final PopularProjectsDtoMapper popularProjectsDtoMapper;
    private final FilterProjectDtoMapper filterProjectDtoMapper;
    private final ContinueProjectDtoMapper continueProjectDtoMapper;

    private final ProjectRepositoryPort projectRepositoryPort;
    private final ProjectRealTimeSearchPort projectRealTimeSearchPort;
    private final ProjectSimilarSearchPort projectSimilarSearchPort;
    private final ProjectQueryRepositoryPort projectQueryRepositoryPort;
    private final ProjectViewCountRedisPort projectViewCountRedisPort;

    private final FindUsernameUseCase findUsernameUseCase;
    private final FindUserThumbnailUseCase findUserThumbnailUseCase;
    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;
    private final GetUserInfoUseCase getUserInfoUseCase;
    private final ConnectedProjectAssociatedDtoMapper connectedProjectAssociatedDtoMapper;
    private final ValidateTargetLikeUseCase validateTargetLikeUseCase;

    private static final String VIEW_TARGET_TYPE = "PROJECT";

    /**
     * 주어진 키워드로 실시간 프로젝트를 검색하여 결과 목록을 반환합니다.
     *
     * 키워드가 null이거나 공백만 포함된 경우 빈 리스트를 반환합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 최대 결과 개수
     * @return 키워드에 매칭되는 프로젝트의 실시간 검색 응답 객체 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectRealTimeSearchResponse> searchByKeyword(String keyword, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
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
     * 지정한 개수만큼 인기 프로젝트를 조회하고, 각 프로젝트에 사용자명과 주제, 분석 목적, 데이터 소스, 저자 레벨 등 라벨 정보를 포함한 응답 리스트를 반환합니다.
     *
     * @param size 조회할 인기 프로젝트의 최대 개수
     * @return 사용자명과 다양한 라벨 정보가 포함된 인기 프로젝트 응답 리스트
     */

    @Override
    @Transactional(readOnly = true)
    public List<ProjectPopularSearchResponse> findPopularProjects(int size) {
        List<Project> savedProjects = projectQueryRepositoryPort.findPopularProjects(size);

        ProjectLabelMappingResponse labelResponse = labelMapping(savedProjects);

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
     * 필터 조건과 페이지 정보를 기반으로 프로젝트 목록을 조회하고, 각 프로젝트에 사용자명 및 다양한 라벨 정보를 매핑하여 페이지 형태로 반환합니다.
     *
     * @param request 프로젝트 필터링 조건이 포함된 요청 객체
     * @param pageable 페이지네이션 및 정렬 정보
     * @return 필터링된 프로젝트 응답 DTO의 페이지 객체
     */
    @Override
    public Page<ProjectFilterResponse> findFilterdProjects(ProjectFilterRequest request, Pageable pageable) {
        ProjectSortType sortType = (request.sortType() != null && !request.sortType().isEmpty())
                ? ProjectSortType.of(request.sortType())
                : null;
        Page<Project> savedProjects = projectQueryRepositoryPort.searchByFilters(request, pageable, sortType);

        ProjectLabelMappingResponse labelResponse = labelMapping(savedProjects.getContent());
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
     * 프로젝트 컬렉션에서 사용자, 토픽, 분석 목적, 데이터 소스, 저자 레벨의 ID를 추출하여 각 ID에 해당하는 사용자명과 레이블 정보를 일괄 조회합니다.
     *
     * @param savedProjects 사용자명 및 레이블 매핑 정보를 조회할 프로젝트 컬렉션
     * @return 각 ID에 대한 사용자명과 레이블 매핑 정보를 포함하는 ProjectLabelMappingResponse 객체
     */
    private ProjectLabelMappingResponse labelMapping(Collection<Project> savedProjects) {
        List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
        List<Long> analysisPurposeIds = savedProjects.stream().map(Project::getAnalysisPurposeId).toList();
        List<Long> dataSourceIds = savedProjects.stream().map(Project::getDataSourceId).toList();
        List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

        return new ProjectLabelMappingResponse(
                findUsernameUseCase.findUsernamesByIds(userIds),
                getTopicLabelFromIdUseCase.getLabelsByIds(topicIds),
                getAnalysisPurposeLabelFromIdUseCase.getLabelsByIds(analysisPurposeIds),
                getDataSourceLabelFromIdUseCase.getLabelsByIds(dataSourceIds),
                getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds)
        );
    }

    /**
     * 지정한 프로젝트의 상세 정보를 조회합니다.
     *
     * 프로젝트의 기본 정보, 작성자 이름, 작성자 레벨 및 직업 라벨, 주제, 분석 목적, 데이터 소스 라벨, 메타데이터(제목, 내용, 파일 URL, 생성일, 댓글/좋아요/조회수), 사용자의 좋아요 여부, 자식 프로젝트 및 데이터 존재 여부를 포함한 상세 정보를 반환합니다. 조회자 식별자를 기반으로 프로젝트의 조회수를 1회 증가시킵니다.
     *
     * @param projectId 상세 정보를 조회할 프로젝트의 ID
     * @param userId 프로젝트를 조회하는 사용자의 ID (좋아요 여부 확인에 사용, null 가능)
     * @param viewerId 프로젝트를 조회하는 사용자의 식별자(조회수 중복 방지에 사용)
     * @return 프로젝트의 상세 정보를 담은 ProjectDetailResponse 객체
     * @throws ProjectException 프로젝트가 존재하지 않을 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetail(Long projectId, Long userId, String viewerId) {

        // 프로젝트 세부정보 조회
        Project project = projectQueryRepositoryPort.findProjectById(projectId)
                .orElseThrow(() -> new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT));

        boolean hasChild = projectQueryRepositoryPort.existsByParentProjectId(projectId);
        boolean hasData = projectQueryRepositoryPort.existsProjectDataByProjectId(projectId);

        UserInfo userInfo = getUserInfoUseCase.getUserInfo(project.getUserId());
        ProjectUser projectUser = ProjectUser.from(userInfo);

        // 선택조건 null 일 경우에 대한 처리
        String authorLevelLabel = projectUser.authorLevelId() == null ? null : getAuthorLevelLabelFromIdUseCase.getLabelById(projectUser.authorLevelId());
        String occupationLabel = projectUser.occupationId() == null ? null : getOccupationLabelFromIdUseCase.getLabelById(projectUser.occupationId());

        boolean isLiked = false;
        if (userId != null) {
            isLiked = validateTargetLikeUseCase.hasUserLikedTarget(userId, projectId, TargetType.PROJECT);
        }

        // 프로젝트 조회수 증가
        // 조회수 기록 (중복 방지 TTL)
        projectViewCountRedisPort.increaseViewCount(projectId, viewerId, VIEW_TARGET_TYPE);

        return new ProjectDetailResponse(
                project.getId(),
                project.getTitle(),
                findUsernameUseCase.findUsernameById(project.getUserId()),
                authorLevelLabel,
                occupationLabel,
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
                isLiked,
                hasChild,
                hasData
        );
    }

    /**
     * 지정한 프로젝트 ID를 기준으로 이어지는 프로젝트 목록을 페이지 단위로 조회합니다.
     *
     * 각 프로젝트에 대해 작성자 이름, 작성자 썸네일, 주제 라벨, 저자 레벨 라벨 정보를 함께 반환합니다.
     *
     * @param projectId 기준이 되는 프로젝트의 ID
     * @param pageable 페이지네이션 정보
     * @return 이어지는 프로젝트 응답 DTO의 페이지 객체
     */
    @Override
    public Page<ContinueProjectResponse> findContinueProjects(Long projectId, Pageable pageable) {
        Page<Project> savedProjects = projectQueryRepositoryPort.findContinueProjects(projectId, pageable);

        List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
        List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

        Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(userIds);
        Map<Long, String> userThumbnailMap = findUserThumbnailUseCase.findUserThumbnailsByIds(userIds);
        Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);
        Map<Long, String> authorLevelLabelMap = getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);

        return savedProjects.map(project -> continueProjectDtoMapper.toResponseDto(
                project,
                usernameMap.get(project.getUserId()),
                userThumbnailMap.get(project.getUserId()),
                topicLabelMap.get(project.getTopicId()),
                authorLevelLabelMap.get(project.getAuthorLevelId())
        ));
    }

    /**
     * 지정된 데이터 ID와 연결된 프로젝트들을 페이지 단위로 조회하여, 각 프로젝트의 사용자명과 토픽 라벨 정보를 포함한 응답으로 반환합니다.
     *
     * @param dataId 연결된 데이터를 식별하는 ID
     * @param pageable 페이지네이션 정보
     * @return 연결된 프로젝트의 상세 정보가 담긴 페이지 객체
     */
    @Override
    public Page<ConnectedProjectAssociatedWithDataResponse> findConnectedProjects(Long dataId, Pageable pageable) {
        Page<Project> savedProjects = projectQueryRepositoryPort.findConnectedProjectsAssociatedWithData(dataId, pageable);

        List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();

        Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(userIds);
        Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);

        return savedProjects.map(project -> connectedProjectAssociatedDtoMapper.toResponseDto(
                project,
                usernameMap.get(project.getUserId()),
                topicLabelMap.get(project.getTopicId())
        ));
    }

    /**
     * 주어진 프로젝트 ID에 해당하는 사용자 ID를 반환합니다.
     *
     * @param projectId 사용자 ID를 조회할 프로젝트의 ID
     * @return 해당 프로젝트의 사용자 ID
     */
    @Override
    @Transactional(readOnly = true)
    public Long findUserIdByProjectId(Long projectId) {
        return projectRepositoryPort.findUserIdByProjectId(projectId);
    }

    /**
     * 삭제된 프로젝트를 포함하여 지정된 프로젝트 ID의 소유자 사용자 ID를 반환합니다.
     *
     * @param projectId 사용자 ID를 조회할 프로젝트의 ID
     * @return 해당 프로젝트(삭제된 경우 포함)의 소유자 사용자 ID
     */
    @Override
    @Transactional(readOnly = true)
    public Long findUserIdIncludingDeleted(Long projectId) {
        return projectRepositoryPort.findUserIdIncludingDeleted(projectId);
    }

    /**
     * 주어진 프로젝트 ID에 해당하는 프로젝트가 존재하는지 검증합니다.
     *
     * 프로젝트가 존재하지 않을 경우 {@code ProjectException}을 발생시킵니다.
     *
     * @param projectId 검증할 프로젝트의 ID
     * @throws ProjectException 프로젝트가 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateProject(Long projectId) {
        boolean isValidate = projectRepositoryPort.existsProjectById(projectId);
        if (!isValidate) {
            throw new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
        }
    }
}
