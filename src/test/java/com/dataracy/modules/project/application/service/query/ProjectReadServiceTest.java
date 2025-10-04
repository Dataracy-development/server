package com.dataracy.modules.project.application.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dataracy.modules.dataset.application.dto.response.read.ConnectedDataResponse;
import com.dataracy.modules.dataset.application.port.in.query.read.FindConnectedDataSetsUseCase;
import com.dataracy.modules.like.application.port.in.validate.ValidateTargetLikeUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectWithDataIdsResponse;
import com.dataracy.modules.project.application.mapper.read.ConnectedProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.ContinuedProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.PopularProjectDtoMapper;
import com.dataracy.modules.project.application.mapper.read.ProjectDetailDtoMapper;
import com.dataracy.modules.project.application.mapper.support.ParentProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.query.extractor.FindProjectLabelMapUseCase;
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
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.occupation.GetOccupationLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.GetUserInfoUseCase;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.vo.UserInfo;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectReadServiceTest {

  @InjectMocks private ProjectReadService service;

  @Mock private ContinuedProjectDtoMapper continuedProjectDtoMapper;

  @Mock private ConnectedProjectDtoMapper connectedProjectDtoMapper;

  @Mock private PopularProjectDtoMapper popularProjectDtoMapper;

  @Mock private ProjectDetailDtoMapper projectDetailDtoMapper;

  @Mock private ParentProjectDtoMapper parentProjectDtoMapper;

  @Mock private ManageProjectViewCountPort manageProjectViewCountPort;

  @Mock private CheckProjectExistsByParentPort checkProjectExistsByParentPort;

  @Mock private FindProjectPort findProjectPort;

  @Mock private FindContinuedProjectsPort findContinuedProjectsPort;

  @Mock private FindConnectedProjectsPort findConnectedProjectsPort;

  @Mock private GetPopularProjectsPort getPopularProjectsPort;

  @Mock private GetUserInfoUseCase getUserInfoUseCase;

  @Mock private FindUsernameUseCase findUsernameUseCase;

  @Mock private FindUserThumbnailUseCase findUserThumbnailUseCase;

  @Mock private GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;

  @Mock private GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;

  @Mock private GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;

  @Mock private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

  @Mock private GetOccupationLabelFromIdUseCase getOccupationLabelFromIdUseCase;

  @Mock private FindProjectLabelMapUseCase findProjectLabelMapUseCase;

  @Mock private FindConnectedDataSetsUseCase findConnectedDataSetsUseCase;

  @Mock private ValidateTargetLikeUseCase validateTargetLikeUseCase;

  @Mock private PopularProjectsStoragePort popularProjectsStoragePort;

  @Mock private UpdatePopularProjectsStorageUseCase updatePopularProjectsStorageUseCase;

  private Project createSampleProject() {
    return Project.builder()
        .id(1L)
        .title("Test Project")
        .content("Test Description")
        .userId(1L)
        .topicId(1L)
        .analysisPurposeId(1L)
        .dataSourceId(1L)
        .authorLevelId(1L)
        .parentProjectId(null)
        .thumbnailUrl("test-url")
        .createdAt(LocalDateTime.now())
        .build();
  }

  private ProjectWithDataIdsResponse createSampleProjectWithDataIds() {
    return new ProjectWithDataIdsResponse(createSampleProject(), List.of(1L, 2L));
  }

  private UserInfo createSampleUserInfo() {
    return new UserInfo(
        1L,
        RoleType.ROLE_USER,
        "test@example.com",
        "testuser",
        1L,
        1L,
        List.of(1L, 2L),
        1L,
        "profile-url",
        "Test Introduction");
  }

  @Nested
  @DisplayName("프로젝트 상세 조회")
  class GetProjectDetail {

    @Test
    @DisplayName("프로젝트 상세 조회 성공")
    void getProjectDetailSuccess() {
      // given
      Long projectId = 1L;
      Long userId = 1L;
      String viewerId = "viewer1";
      ProjectWithDataIdsResponse projectWithDataIds = createSampleProjectWithDataIds();
      UserInfo userInfo = createSampleUserInfo();
      ConnectedDataResponse connectedData =
          new ConnectedDataResponse(
              1L,
              "Data Title",
              1L,
              "creator",
              "profile-url",
              "topic",
              "dataType",
              LocalDate.now(),
              LocalDate.now(),
              "thumb-url",
              10,
              1024L,
              5,
              100,
              LocalDateTime.now(),
              1L);

      given(findProjectPort.findProjectWithDataById(projectId))
          .willReturn(Optional.of(projectWithDataIds));
      given(findConnectedDataSetsUseCase.findDataSetsByIds(anyList()))
          .willReturn(List.of(connectedData));
      given(checkProjectExistsByParentPort.checkParentProjectExistsById(projectId))
          .willReturn(true);
      given(getUserInfoUseCase.extractUserInfo(anyLong())).willReturn(userInfo);
      given(getAuthorLevelLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Author Level");
      given(getOccupationLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Occupation");
      given(validateTargetLikeUseCase.hasUserLikedTarget(userId, projectId, TargetType.PROJECT))
          .willReturn(true);
      given(getTopicLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Topic");
      given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(anyLong()))
          .willReturn("Analysis Purpose");
      given(getDataSourceLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Data Source");
      willDoNothing()
          .given(manageProjectViewCountPort)
          .increaseViewCount(anyLong(), anyString(), anyString());
      given(
              projectDetailDtoMapper.toResponseDto(
                  any(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyBoolean(),
                  anyBoolean(),
                  anyList(),
                  any()))
          .willReturn(
              new ProjectDetailResponse(
                  1L,
                  "Test Project",
                  1L,
                  "testuser",
                  "profile-url",
                  "Test Introduction",
                  "Author Level",
                  "Occupation",
                  "Topic",
                  "Analysis Purpose",
                  "Data Source",
                  true,
                  1L,
                  "content",
                  "test-url",
                  LocalDateTime.now(),
                  0L,
                  0L,
                  0L,
                  true,
                  true,
                  List.of(),
                  null));

      // when
      ProjectDetailResponse response = service.getProjectDetail(projectId, userId, viewerId);

      // then
      assertThat(response.title()).isEqualTo("Test Project");
      assertThat(response.creatorName()).isEqualTo("testuser");
      assertThat(response.isLiked()).isTrue();
      assertThat(response.hasChild()).isTrue();
      then(manageProjectViewCountPort).should().increaseViewCount(projectId, viewerId, "PROJECT");
    }

    @Test
    @DisplayName("프로젝트 상세 조회 실패 - 프로젝트가 존재하지 않음")
    void getProjectDetailFailProjectNotFound() {
      // given
      Long projectId = 999L;
      Long userId = 1L;
      String viewerId = "viewer1";

      given(findProjectPort.findProjectWithDataById(projectId)).willReturn(Optional.empty());

      // when & then
      ProjectException ex =
          catchThrowableOfType(
              () -> service.getProjectDetail(projectId, userId, viewerId), ProjectException.class);
      assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
    }

    @Test
    @DisplayName("프로젝트 상세 조회 성공 - null 사용자 ID")
    void getProjectDetailSuccessWithNullUserId() {
      // given
      Long projectId = 1L;
      String viewerId = "viewer1";
      ProjectWithDataIdsResponse projectWithDataIds = createSampleProjectWithDataIds();
      UserInfo userInfo = createSampleUserInfo();
      ConnectedDataResponse connectedData =
          new ConnectedDataResponse(
              1L,
              "Data Title",
              1L,
              "creator",
              "profile-url",
              "topic",
              "dataType",
              LocalDate.now(),
              LocalDate.now(),
              "thumb-url",
              10,
              1024L,
              5,
              100,
              LocalDateTime.now(),
              1L);

      given(findProjectPort.findProjectWithDataById(projectId))
          .willReturn(Optional.of(projectWithDataIds));
      given(findConnectedDataSetsUseCase.findDataSetsByIds(anyList()))
          .willReturn(List.of(connectedData));
      given(checkProjectExistsByParentPort.checkParentProjectExistsById(projectId))
          .willReturn(false);
      given(getUserInfoUseCase.extractUserInfo(anyLong())).willReturn(userInfo);
      given(getAuthorLevelLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Author Level");
      given(getOccupationLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Occupation");
      given(getTopicLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Topic");
      given(getAnalysisPurposeLabelFromIdUseCase.getLabelById(anyLong()))
          .willReturn("Analysis Purpose");
      given(getDataSourceLabelFromIdUseCase.getLabelById(anyLong())).willReturn("Data Source");
      willDoNothing()
          .given(manageProjectViewCountPort)
          .increaseViewCount(anyLong(), anyString(), anyString());
      given(
              projectDetailDtoMapper.toResponseDto(
                  any(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyBoolean(),
                  anyBoolean(),
                  anyList(),
                  any()))
          .willReturn(
              new ProjectDetailResponse(
                  1L,
                  "Test Project",
                  1L,
                  "testuser",
                  "profile-url",
                  "Test Introduction",
                  "Author Level",
                  "Occupation",
                  "Topic",
                  "Analysis Purpose",
                  "Data Source",
                  false,
                  null,
                  "content",
                  "test-url",
                  LocalDateTime.now(),
                  0L,
                  0L,
                  0L,
                  false,
                  false,
                  List.of(),
                  null));

      // when
      ProjectDetailResponse response = service.getProjectDetail(projectId, null, viewerId);

      // then
      assertThat(response.isLiked()).isFalse();
      then(validateTargetLikeUseCase)
          .should(never())
          .hasUserLikedTarget(anyLong(), anyLong(), any(TargetType.class));
    }
  }

  @Nested
  @DisplayName("이어가기 프로젝트 조회")
  class FindContinuedProjects {

    @Test
    @DisplayName("이어가기 프로젝트 조회 성공")
    void findContinuedProjectsSuccess() {
      // given
      Long projectId = 1L;
      Pageable pageable = PageRequest.of(0, 10);
      Project project = createSampleProject();
      Page<Project> projectPage = new PageImpl<>(List.of(project));

      given(findContinuedProjectsPort.findContinuedProjects(projectId, pageable))
          .willReturn(projectPage);
      given(findUsernameUseCase.findUsernamesByIds(anyList())).willReturn(Map.of(1L, "testuser"));
      given(findUserThumbnailUseCase.findUserThumbnailsByIds(anyList()))
          .willReturn(Map.of(1L, "profile-url"));
      given(getTopicLabelFromIdUseCase.getLabelsByIds(anyList())).willReturn(Map.of(1L, "Topic"));
      given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(anyList()))
          .willReturn(Map.of(1L, "Author Level"));
      given(
              continuedProjectDtoMapper.toResponseDto(
                  any(), anyString(), anyString(), anyString(), anyString()))
          .willReturn(
              new ContinuedProjectResponse(
                  1L,
                  "Test Project",
                  1L,
                  "testuser",
                  "profile-url",
                  "test-url",
                  "Topic",
                  "Author Level",
                  0L,
                  0L,
                  0L,
                  LocalDateTime.now()));

      // when
      Page<ContinuedProjectResponse> response = service.findContinuedProjects(projectId, pageable);

      // then
      assertThat(response.getContent()).hasSize(1);
      assertThat(response.getContent().get(0).title()).isEqualTo("Test Project");
    }
  }

  @Nested
  @DisplayName("연결된 프로젝트 조회")
  class FindConnectedProjects {

    @Test
    @DisplayName("연결된 프로젝트 조회 성공")
    void findConnectedProjectsSuccess() {
      // given
      Long dataId = 1L;
      Pageable pageable = PageRequest.of(0, 10);
      Project project = createSampleProject();
      Page<Project> projectPage = new PageImpl<>(List.of(project));

      given(findConnectedProjectsPort.findConnectedProjectsAssociatedWithDataset(dataId, pageable))
          .willReturn(projectPage);
      given(findUsernameUseCase.findUsernamesByIds(anyList())).willReturn(Map.of(1L, "testuser"));
      given(findUserThumbnailUseCase.findUserThumbnailsByIds(anyList()))
          .willReturn(Map.of(1L, "profile-url"));
      given(getTopicLabelFromIdUseCase.getLabelsByIds(anyList())).willReturn(Map.of(1L, "Topic"));
      given(connectedProjectDtoMapper.toResponseDto(any(), anyString(), anyString(), anyString()))
          .willReturn(
              new ConnectedProjectResponse(
                  1L,
                  "Test Project",
                  1L,
                  "testuser",
                  "profile-url",
                  "Topic",
                  0L,
                  0L,
                  0L,
                  LocalDateTime.now()));

      // when
      Page<ConnectedProjectResponse> response = service.findConnectedProjects(dataId, pageable);

      // then
      assertThat(response.getContent()).hasSize(1);
      assertThat(response.getContent().get(0).title()).isEqualTo("Test Project");
    }
  }

  @Nested
  @DisplayName("인기 프로젝트 조회")
  class GetPopularProjects {

    @Test
    @DisplayName("인기 프로젝트 조회 성공 - 캐시 히트")
    void getPopularProjectsSuccessCacheHit() {
      // given
      int size = 5;
      PopularProjectResponse cachedProject =
          new PopularProjectResponse(
              1L,
              "Cached Project",
              "content",
              1L,
              "testuser",
              "profile-url",
              "test-url",
              "Topic",
              "Analysis Purpose",
              "Data Source",
              "Author Level",
              0L,
              0L,
              0L);

      given(popularProjectsStoragePort.getPopularProjects())
          .willReturn(Optional.of(List.of(cachedProject)));

      // when
      List<PopularProjectResponse> response = service.getPopularProjects(size);

      // then
      assertThat(response).hasSize(1);
      assertThat(response.get(0).title()).isEqualTo("Cached Project");
      then(getPopularProjectsPort).should(never()).getPopularProjects(anyInt());
      then(updatePopularProjectsStorageUseCase).should(never()).warmUpCacheIfNeeded(anyInt());
    }

    @Test
    @DisplayName("인기 프로젝트 조회 성공 - 캐시 미스")
    void getPopularProjectsSuccessCacheMiss() {
      // given
      int size = 5;
      Project project = createSampleProject();
      ProjectLabelMapResponse labelResponse =
          new ProjectLabelMapResponse(
              Map.of(1L, "testuser"),
              Map.of(1L, "profile-url"),
              Map.of(1L, "Topic"),
              Map.of(1L, "Analysis Purpose"),
              Map.of(1L, "Data Source"),
              Map.of(1L, "Author Level"));

      given(popularProjectsStoragePort.getPopularProjects()).willReturn(Optional.empty());
      given(getPopularProjectsPort.getPopularProjects(size)).willReturn(List.of(project));
      given(findProjectLabelMapUseCase.labelMapping(anyList())).willReturn(labelResponse);
      given(
              popularProjectDtoMapper.toResponseDto(
                  any(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString()))
          .willReturn(
              new PopularProjectResponse(
                  1L,
                  "Test Project",
                  "content",
                  1L,
                  "testuser",
                  "profile-url",
                  "test-url",
                  "Topic",
                  "Analysis Purpose",
                  "Data Source",
                  "Author Level",
                  0L,
                  0L,
                  0L));
      willDoNothing().given(updatePopularProjectsStorageUseCase).warmUpCacheIfNeeded(anyInt());

      // when
      List<PopularProjectResponse> response = service.getPopularProjects(size);

      // then
      assertThat(response).hasSize(1);
      assertThat(response.get(0).title()).isEqualTo("Test Project");
      then(updatePopularProjectsStorageUseCase)
          .should()
          .warmUpCacheIfNeeded(20); // Math.max(size, 20)
    }
  }

  @Nested
  @DisplayName("경계값 테스트")
  class BoundaryTests {

    @Test
    @DisplayName("음수 프로젝트 ID로 상세 조회")
    void getProjectDetailWithNegativeId() {
      // given
      Long negativeProjectId = -1L;
      Long userId = 1L;
      String viewerId = "viewer1";

      given(findProjectPort.findProjectWithDataById(negativeProjectId))
          .willReturn(Optional.empty());

      // when & then
      ProjectException ex =
          catchThrowableOfType(
              () -> service.getProjectDetail(negativeProjectId, userId, viewerId),
              ProjectException.class);
      assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
    }

    @Test
    @DisplayName("빈 페이지로 이어가기 프로젝트 조회")
    void findContinuedProjectsWithEmptyPage() {
      // given
      Long projectId = 1L;
      Pageable pageable = PageRequest.of(0, 10);
      Page<Project> emptyPage = new PageImpl<>(List.of());

      given(findContinuedProjectsPort.findContinuedProjects(projectId, pageable))
          .willReturn(emptyPage);
      given(findUsernameUseCase.findUsernamesByIds(anyList())).willReturn(Map.of());
      given(findUserThumbnailUseCase.findUserThumbnailsByIds(anyList())).willReturn(Map.of());
      given(getTopicLabelFromIdUseCase.getLabelsByIds(anyList())).willReturn(Map.of());
      given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(anyList())).willReturn(Map.of());

      // when
      Page<ContinuedProjectResponse> response = service.findContinuedProjects(projectId, pageable);

      // then
      assertThat(response.getContent()).isEmpty();
    }

    @Test
    @DisplayName("크기 0으로 인기 프로젝트 조회")
    void getPopularProjectsWithZeroSize() {
      // given
      int size = 0;
      Project project = createSampleProject();
      ProjectLabelMapResponse labelResponse =
          new ProjectLabelMapResponse(Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());

      given(popularProjectsStoragePort.getPopularProjects()).willReturn(Optional.empty());
      given(getPopularProjectsPort.getPopularProjects(size)).willReturn(List.of(project));
      given(findProjectLabelMapUseCase.labelMapping(anyList())).willReturn(labelResponse);
      given(
              popularProjectDtoMapper.toResponseDto(
                  any(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString()))
          .willReturn(
              new PopularProjectResponse(
                  1L,
                  "Test Project",
                  "content",
                  1L,
                  null,
                  null,
                  "test-url",
                  null,
                  null,
                  null,
                  null,
                  0L,
                  0L,
                  0L));
      willDoNothing().given(updatePopularProjectsStorageUseCase).warmUpCacheIfNeeded(anyInt());

      // when
      List<PopularProjectResponse> response = service.getPopularProjects(size);

      // then
      assertThat(response).hasSize(1);
      then(updatePopularProjectsStorageUseCase).should().warmUpCacheIfNeeded(20); // Math.max(0, 20)
    }
  }
}
