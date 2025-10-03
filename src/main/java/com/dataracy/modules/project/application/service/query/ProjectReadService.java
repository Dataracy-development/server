/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.service.query;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.dataset.application.port.in.query.read.FindConnectedDataSetsUseCase;
import com.dataracy.modules.like.application.port.in.validate.ValidateTargetLikeUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectWithDataIdsResponse;
import com.dataracy.modules.project.application.mapper.read.ConnectedProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.ContinuedProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.PopularProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.ProjectDetailDtoMapper;
import com.dataracy.modules.project.application.mapper.support.ParentProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.query.extractor.FindProjectLabelMapUseCase;
import com.dataracy.modules.project.application.port.in.query.read.FindConnectedProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.FindContinuedProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.GetPopularProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.GetProjectDetailUseCase;
import com.dataracy.modules.project.application.port.in.storage.UpdatePopularProjectsStorageUseCase;
import com.dataracy.modules.project.application.port.out.query.read.FindConnectedProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.FindContinuedProjectsPort;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.application.port.out.query.read.GetPopularProjectsPort;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByParentPort;
import com.dataracy.modules.project.application.port.out.storage.PopularProjectsStoragePort;
import com.dataracy.modules.project.application.port.out.view.ManageProjectViewCountPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.model.vo.ProjectUser;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectReadService
    implements GetProjectDetailUseCase,
        FindContinuedProjectsUseCase,
        FindConnectedProjectsUseCase,
        GetPopularProjectsUseCase {
  private final ContinuedProjectDtoMapper continuedProjectDtoMapper;
  private final ConnectedProjectDtoMapper connectedProjectDtoMapper;
  private final PopularProjectDtoMapper popularProjectDtoMapper;
  private final ProjectDetailDtoMapper projectDetailDtoMapper;
  private final ParentProjectDtoMapper parentProjectDtoMapper;

  private final ManageProjectViewCountPort manageProjectViewCountPort;

  private final CheckProjectExistsByParentPort checkProjectExistsByParentPort;

  private final FindProjectPort findProjectPort;
  private final FindContinuedProjectsPort findContinuedProjectsPort;
  private final FindConnectedProjectsPort findConnectedProjectsPort;
  private final GetPopularProjectsPort getPopularProjectsPort;

  // Use Case 상수 정의
  private static final String GET_PROJECT_DETAIL_USE_CASE = "GetProjectDetailUseCase";
  private static final String FIND_CONTINUED_PROJECTS_USE_CASE = "FindContinuedProjectsUseCase";
  private static final String FIND_CONNECTED_PROJECTS_USE_CASE = "FindConnectedProjectsUseCase";
  private static final String GET_POPULAR_PROJECTS_USE_CASE = "GetPopularProjectsUseCase";

  // 메시지 상수 정의
  private static final String PROJECT_NOT_FOUND_MESSAGE = "해당 프로젝트가 존재하지 않습니다. projectId=";

  private final GetUserInfoUseCase getUserInfoUseCase;
  private final FindUsernameUseCase findUsernameUseCase;
  private final FindUserThumbnailUseCase findUserThumbnailUseCase;

  private final GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;
  private final GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;
  private final GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;
  private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
  private final GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;
  private final FindProjectLabelMapUseCase findProjectLabelMapUseCase;

  private final FindConnectedDataSetsUseCase findConnectedDataSetsUseCase;
  private final ValidateTargetLikeUseCase validateTargetLikeUseCase;

  // 저장소 관련 의존성
  private final PopularProjectsStoragePort popularProjectsStoragePort;
  private final UpdatePopularProjectsStorageUseCase updatePopularProjectsStorageUseCase;

  private static final String VIEW_TARGET_TYPE = "PROJECT";

  /**
   * 지정한 프로젝트의 전체 상세 정보를 조회하여 반환합니다.
   *
   * <p>반환되는 상세 정보에는 프로젝트의 기본 메타데이터(제목·내용·파일 URL·생성일·댓글/좋아요/조회수), 작성자 정보(닉네임·소개·프로필 이미지 URL·작성자
   * 레벨·직업), 주제·분석 목적·데이터 소스, 사용자의 좋아요 여부(조회자 userId가 주어졌을 때), 자식 프로젝트 존재 여부, 연결된 데이터셋 목록, 그리고 부모
   * 프로젝트 정보(존재하는 경우)가 포함됩니다. viewerId를 기반으로 조회수는 중복 방지 로직을 거쳐 1회 증가합니다.
   *
   * @param projectId 상세 정보를 조회할 프로젝트의 ID
   * @param userId 조회자 사용자 ID (좋아요 여부 확인에 사용, null 가능)
   * @param viewerId 조회수 중복 방지를 위한 조회자 식별자
   * @return 프로젝트의 상세 정보를 담은 {@code ProjectDetailResponse}
   * @throws ProjectException 프로젝트 또는 참조된 부모 프로젝트가 존재하지 않을 경우 발생
   */
  @Override
  @Transactional(readOnly = true)
  public ProjectDetailResponse getProjectDetail(Long projectId, Long userId, String viewerId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(GET_PROJECT_DETAIL_USE_CASE, "프로젝트 세부정보 조회 서비스 시작 projectId=" + projectId);

    // 프로젝트 세부정보 조회
    ProjectWithDataIdsResponse projectWithDataIdsResponse =
        findProjectPort
            .findProjectWithDataById(projectId)
            .orElseThrow(
                () -> {
                  LoggerFactory.service()
                      .logWarning(
                          GET_PROJECT_DETAIL_USE_CASE, PROJECT_NOT_FOUND_MESSAGE + projectId);
                  return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                });

    Project project = projectWithDataIdsResponse.project();
    List<Long> dataIds = projectWithDataIdsResponse.dataIds();

    List<ProjectConnectedDataResponse> connectedDataSets =
        findConnectedDataSetsUseCase.findDataSetsByIds(dataIds).stream()
            .map(ProjectConnectedDataResponse::from)
            .toList();

    boolean hasChild = checkProjectExistsByParentPort.checkParentProjectExistsById(projectId);

    // 작성자 정보
    UserInfo userInfo = getUserInfoUseCase.extractUserInfo(project.getUserId());
    ProjectUser projectUser = ProjectUser.fromUserInfo(userInfo);

    // 선택조건 null 일 경우에 대한 처리
    String authorLevelLabel =
        projectUser.authorLevelId() == null
            ? null
            : getAuthorLevelLabelFromIdUseCase.getLabelById(projectUser.authorLevelId());
    String occupationLabel =
        projectUser.occupationId() == null
            ? null
            : getOccupationLabelFromIdUseCase.getLabelById(projectUser.occupationId());

    boolean isLiked = false;
    if (userId != null) {
      isLiked = validateTargetLikeUseCase.hasUserLikedTarget(userId, projectId, TargetType.PROJECT);
    }

    Project parentProject =
        project.getParentProjectId() == null
            ? null
            : findProjectPort
                .findProjectById(project.getParentProjectId())
                .orElseThrow(
                    () -> {
                      LoggerFactory.service()
                          .logWarning(
                              GET_PROJECT_DETAIL_USE_CASE,
                              PROJECT_NOT_FOUND_MESSAGE + project.getParentProjectId());
                      return new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
                    });
    ParentProjectResponse parentProjectResponse =
        parentProject == null
            ? null
            : parentProjectDtoMapper.toResponseDto(
                parentProject,
                findUsernameUseCase.findUsernameById(parentProject.getUserId()),
                findUserThumbnailUseCase.findUserThumbnailById(parentProject.getUserId()));

    // 프로젝트 조회수 증가
    // 조회수 기록 (중복 방지 TTL)
    manageProjectViewCountPort.increaseViewCount(projectId, viewerId, VIEW_TARGET_TYPE);

    ProjectDetailResponse projectDetailResponse =
        projectDetailDtoMapper.toResponseDto(
            project,
            projectUser.nickname(),
            projectUser.profileImageUrl(),
            projectUser.introductionText(),
            authorLevelLabel,
            occupationLabel,
            getTopicLabelFromIdUseCase.getLabelById(project.getTopicId()),
            getAnalysisPurposeLabelFromIdUseCase.getLabelById(project.getAnalysisPurposeId()),
            getDataSourceLabelFromIdUseCase.getLabelById(project.getDataSourceId()),
            isLiked,
            hasChild,
            connectedDataSets,
            parentProjectResponse);

    LoggerFactory.service()
        .logSuccess(
            GET_PROJECT_DETAIL_USE_CASE, "프로젝트 세부정보 조회 서비스 종료 projectId=" + projectId, startTime);
    return projectDetailResponse;
  }

  /**
   * 주어진 프로젝트를 기준으로 이어지는(파생된) 프로젝트들을 페이지 단위로 조회하여 반환합니다.
   *
   * <p>반환되는 각 항목에는 작성자 이름, 작성자 썸네일 URL, 주제 라벨, 저자 레벨 라벨이 포함됩니다.
   *
   * @param projectId 이어지는 프로젝트를 조회할 기준이 되는 프로젝트의 ID
   * @param pageable 조회할 페이지와 정렬 정보
   * @return 이어지는 프로젝트 정보를 담은 Page<ContinuedProjectResponse>
   */
  @Override
  @Transactional(readOnly = true)
  public Page<ContinuedProjectResponse> findContinuedProjects(Long projectId, Pageable pageable) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                FIND_CONTINUED_PROJECTS_USE_CASE, "이어가기 프로젝트 목록 조회 서비스 시작 projectId=" + projectId);

    Page<Project> savedProjects =
        findContinuedProjectsPort.findContinuedProjects(projectId, pageable);

    List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
    List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();
    List<Long> authorLevelIds = savedProjects.stream().map(Project::getAuthorLevelId).toList();

    Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(userIds);
    Map<Long, String> userProfileUrlMap = findUserThumbnailUseCase.findUserThumbnailsByIds(userIds);
    Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);
    Map<Long, String> authorLevelLabelMap =
        getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);

    Page<ContinuedProjectResponse> findContinuedProjectsResponse =
        savedProjects.map(
            project ->
                continuedProjectDtoMapper.toResponseDto(
                    project,
                    usernameMap.get(project.getUserId()),
                    userProfileUrlMap.get(project.getUserId()),
                    topicLabelMap.get(project.getTopicId()),
                    authorLevelLabelMap.get(project.getAuthorLevelId())));

    LoggerFactory.service()
        .logSuccess(
            FIND_CONTINUED_PROJECTS_USE_CASE,
            "이어가기 프로젝트 목록 조회 서비스 종료 projectId=" + projectId,
            startTime);
    return findContinuedProjectsResponse;
  }

  /**
   * 지정된 데이터셋과 연결된 프로젝트들을 페이지 단위로 조회하여, 각 프로젝트에 사용자명, 사용자 프로필 이미지 URL, 토픽 라벨을 포함한 응답 페이지를 반환합니다.
   *
   * @param dataId 연결된 프로젝트를 찾을 데이터셋의 식별자
   * @param pageable 페이지 번호·크기·정렬 정보(페이징 조건)
   * @return 각 프로젝트에 사용자명, 사용자 프로필 이미지 URL, 토픽 라벨이 포함된 ConnectedProjectResponse의 페이지
   */
  @Override
  @Transactional(readOnly = true)
  public Page<ConnectedProjectResponse> findConnectedProjects(Long dataId, Pageable pageable) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(
                FIND_CONNECTED_PROJECTS_USE_CASE, "데이터셋과 연결된 프로젝트 목록 조회 서비스 시작 dataId=" + dataId);

    Page<Project> savedProjects =
        findConnectedProjectsPort.findConnectedProjectsAssociatedWithDataset(dataId, pageable);

    List<Long> userIds = savedProjects.stream().map(Project::getUserId).toList();
    List<Long> topicIds = savedProjects.stream().map(Project::getTopicId).toList();

    Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(userIds);
    Map<Long, String> userProfileUrlMap = findUserThumbnailUseCase.findUserThumbnailsByIds(userIds);
    Map<Long, String> topicLabelMap = getTopicLabelFromIdUseCase.getLabelsByIds(topicIds);

    Page<ConnectedProjectResponse> connectedProjectsResponses =
        savedProjects.map(
            project ->
                connectedProjectDtoMapper.toResponseDto(
                    project,
                    usernameMap.get(project.getUserId()),
                    userProfileUrlMap.get(project.getUserId()),
                    topicLabelMap.get(project.getTopicId())));

    LoggerFactory.service()
        .logSuccess(
            FIND_CONNECTED_PROJECTS_USE_CASE,
            "데이터셋과 연결된 프로젝트 목록 조회 서비스 종료 dataId=" + dataId,
            startTime);
    return connectedProjectsResponses;
  }

  /**
   * 지정한 개수만큼 인기 프로젝트를 조회하고, 각 프로젝트에 사용자명과 주제, 분석 목적, 데이터 소스, 저자 레벨 등 라벨 정보를 포함한 응답 리스트를 반환합니다.
   *
   * <p>캐시 우선 조회 후, 캐시가 없으면 DB에서 조회하고 캐시를 워밍업합니다.
   *
   * @param size 조회할 인기 프로젝트의 최대 개수
   * @return 인기 프로젝트에 대한 상세 라벨 정보가 포함된 응답 리스트
   */
  @Override
  @Transactional(readOnly = true)
  public List<PopularProjectResponse> getPopularProjects(int size) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(GET_POPULAR_PROJECTS_USE_CASE, "인기 프로젝트 목록 조회 서비스 시작 size=" + size);

    // 저장소에서 먼저 조회
    LoggerFactory.service().logInfo(GET_POPULAR_PROJECTS_USE_CASE, "저장소에서 캐시 조회 시작");
    var cachedResult = popularProjectsStoragePort.getPopularProjects();
    LoggerFactory.service()
        .logInfo(
            GET_POPULAR_PROJECTS_USE_CASE,
            "저장소 캐시 조회 결과: " + (cachedResult.isPresent() ? "데이터 존재" : "데이터 없음"));
    if (cachedResult.isPresent()) {
      List<PopularProjectResponse> cachedData = cachedResult.get();
      List<PopularProjectResponse> result = cachedData.stream().limit(size).toList();

      LoggerFactory.service()
          .logSuccess(
              GET_POPULAR_PROJECTS_USE_CASE,
              "인기 프로젝트 저장소 조회 성공 size=" + size + " cachedCount=" + cachedData.size(),
              startTime);
      return result;
    }

    // 저장소에 데이터가 없으면 DB에서 조회 (기존 로직)
    LoggerFactory.service()
        .logInfo(GET_POPULAR_PROJECTS_USE_CASE, "저장소에 데이터가 없어 DB에서 조회합니다. size=" + size);

    List<Project> savedProjects = getPopularProjectsPort.getPopularProjects(size);
    ProjectLabelMapResponse labelResponse = findProjectLabelMapUseCase.labelMapping(savedProjects);

    List<PopularProjectResponse> popularProjectResponses =
        savedProjects.stream()
            .map(
                project ->
                    popularProjectDtoMapper.toResponseDto(
                        project,
                        labelResponse.usernameMap().get(project.getUserId()),
                        labelResponse.userProfileUrlMap().get(project.getUserId()),
                        labelResponse.topicLabelMap().get(project.getTopicId()),
                        labelResponse.analysisPurposeLabelMap().get(project.getAnalysisPurposeId()),
                        labelResponse.dataSourceLabelMap().get(project.getDataSourceId()),
                        labelResponse.authorLevelLabelMap().get(project.getAuthorLevelId())))
            .toList();

    // 캐시 워밍업 (비동기) - 데이터셋과 동일한 방식
    updatePopularProjectsStorageUseCase.warmUpCacheIfNeeded(Math.max(size, 20));

    LoggerFactory.service()
        .logSuccess(GET_POPULAR_PROJECTS_USE_CASE, "인기 프로젝트 DB 조회 서비스 종료 size=" + size, startTime);
    return popularProjectResponses;
  }
}
