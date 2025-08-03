package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.application.port.in.ValidateTargetLikeUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.mapper.read.ConnectedProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.ContinuedProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.query.read.FindConnectedProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.FindContinuedProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.GetProjectDetailUseCase;
import com.dataracy.modules.project.application.port.out.cache.CacheProjectViewCountPort;
import com.dataracy.modules.project.application.port.out.query.read.FindConnectedProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.FindContinuedProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectDataExistsPort;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByParentPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.model.vo.ProjectUser;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.profile.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.profile.FindUsernameUseCase;
import com.dataracy.modules.user.application.port.in.profile.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectReadService implements
        GetProjectDetailUseCase,
        FindContinuedProjectsUseCase,
        FindConnectedProjectsUseCase
{
    private final ContinuedProjectDtoMapper continuedProjectDtoMapper;
    private final ConnectedProjectDtoMapper connectedProjectDtoMapper;

    private final CacheProjectViewCountPort cacheProjectViewCountPort;

    private final CheckProjectDataExistsPort checkProjectDataExistsPort;
    private final CheckProjectExistsByParentPort checkProjectExistsByParentPort;

    private final FindProjectPort findProjectPort;
    private final FindContinuedProjectsPort findContinuedProjectsPort;
    private final FindConnectedProjectsPort findConnectedProjectsPort;

    private final GetUserInfoUseCase getUserInfoUseCase;
    private final FindUsernameUseCase findUsernameUseCase;
    private final FindUserThumbnailUseCase findUserThumbnailUseCase;

    private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
    private final GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
    private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;

    private final ValidateTargetLikeUseCase validateTargetLikeUseCase;

    private static final String VIEW_TARGET_TYPE = "PROJECT";

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
        Instant startTime = LoggerFactory.service().logStart("GetProjectDetailUseCase", "프로젝트 세부정보 조회 서비스 시작 projectId=" + projectId);

        // 프로젝트 세부정보 조회
        Project project = findProjectPort.findProjectById(projectId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("GetProjectDetailUseCase", "해당 프로젝트가 존재하지 않습니다. projectId=" + projectId);
                    return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });

        boolean hasChild = checkProjectExistsByParentPort.checkParentProjectExistsById(projectId);
        boolean hasData = checkProjectDataExistsPort.checkProjectDataExistsByProjectId(projectId);

        // 작성자 정보
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
        cacheProjectViewCountPort.increaseViewCount(projectId, viewerId, VIEW_TARGET_TYPE);

        ProjectDetailResponse projectDetailResponse = new ProjectDetailResponse(
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

        LoggerFactory.service().logSuccess("GetProjectDetailUseCase", "프로젝트 세부정보 조회 서비스 종료 projectId=" + projectId, startTime);
        return projectDetailResponse;
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
    public Page<ContinuedProjectResponse> findContinuedProjects(Long projectId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("FindContinuedProjectsUseCase", "이어가기 프로젝트 목록 조회 서비스 시작 projectId=" + projectId);

        Page<Project> savedProjects = findContinuedProjectsPort.findContinuedProjects(projectId, pageable);

        List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
        List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

        Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(userIds);
        Map<Long, String> userThumbnailMap = findUserThumbnailUseCase.findUserThumbnailsByIds(userIds);
        Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);
        Map<Long, String> authorLevelLabelMap = getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);

        Page<ContinuedProjectResponse> findContinuedProjectsResponse = savedProjects.map(project -> continuedProjectDtoMapper.toResponseDto(
                project,
                usernameMap.get(project.getUserId()),
                userThumbnailMap.get(project.getUserId()),
                topicLabelMap.get(project.getTopicId()),
                authorLevelLabelMap.get(project.getAuthorLevelId())
        ));

        LoggerFactory.service().logSuccess("FindContinuedProjectsUseCase", "이어가기 프로젝트 목록 조회 서비스 종료 projectId=" + projectId, startTime);
        return findContinuedProjectsResponse;
    }
    /**
     * 지정된 데이터 ID와 연결된 프로젝트들을 페이지 단위로 조회하여, 각 프로젝트의 사용자명과 토픽 라벨 정보를 포함한 응답으로 반환합니다.
     *
     * @param dataId 연결된 데이터를 식별하는 ID
     * @param pageable 페이지네이션 정보
     * @return 연결된 프로젝트의 상세 정보가 담긴 페이지 객체
     */
    @Override
    public Page<ConnectedProjectResponse> findConnectedProjects(Long dataId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("FindConnectedProjectsUseCase", "데이터셋과 연결된 프로젝트 목록 조회 서비스 시작 dataId=" + dataId);

        Page<Project> savedProjects = findConnectedProjectsPort.findConnectedProjectsAssociatedWithDataset(dataId, pageable);

        List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
        List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();

        Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(userIds);
        Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);

        Page<ConnectedProjectResponse> connectedProjectsResponses = savedProjects.map(project -> connectedProjectDtoMapper.toResponseDto(
                project,
                usernameMap.get(project.getUserId()),
                topicLabelMap.get(project.getTopicId())
        ));

        LoggerFactory.service().logSuccess("FindConnectedProjectsUseCase", "데이터셋과 연결된 프로젝트 목록 조회 서비스 종료 dataId=" + dataId, startTime);
        return connectedProjectsResponses;
    }
}
