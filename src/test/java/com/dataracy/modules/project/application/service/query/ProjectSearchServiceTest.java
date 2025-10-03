/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.application.mapper.search.FilteredProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.query.extractor.FindProjectLabelMapUseCase;
import com.dataracy.modules.project.application.port.out.query.read.FindProjectPort;
import com.dataracy.modules.project.application.port.out.query.search.SearchFilteredProjectsPort;
import com.dataracy.modules.project.application.port.out.query.search.SearchRealTimeProjectsPort;
import com.dataracy.modules.project.application.port.out.query.search.SearchSimilarProjectsPort;
import com.dataracy.modules.project.domain.enums.ProjectSortType;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectSearchServiceTest {

  @Mock private FilteredProjectDtoMapper filteredProjectDtoMapper;

  @Mock private FindProjectPort findProjectPort;

  @Mock private SearchRealTimeProjectsPort searchRealTimeProjectsPort;

  @Mock private SearchSimilarProjectsPort searchSimilarProjectsPort;

  @Mock private SearchFilteredProjectsPort searchFilteredProjectsPort;

  @Mock private FindUsernameUseCase findUsernameUseCase;

  @Mock private FindUserThumbnailUseCase findUserThumbnailUseCase;

  @Mock private FindProjectLabelMapUseCase findProjectLabelMapUseCase;

  @InjectMocks private ProjectSearchService service;

  private MockedStatic<LoggerFactory> loggerFactoryMock;
  private com.dataracy.modules.common.logging.ServiceLogger loggerService;

  @BeforeEach
  void setUp() {
    loggerFactoryMock = mockStatic(LoggerFactory.class);
    loggerService = mock(com.dataracy.modules.common.logging.ServiceLogger.class);
    loggerFactoryMock.when(LoggerFactory::service).thenReturn(loggerService);
    doReturn(Instant.now()).when(loggerService).logStart(anyString(), anyString());
    doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
    doNothing().when(loggerService).logWarning(anyString(), anyString());
  }

  @AfterEach
  void tearDown() {
    if (loggerFactoryMock != null) {
      loggerFactoryMock.close();
    }
  }

  @Nested
  @DisplayName("searchByKeyword 메서드 테스트")
  class SearchByKeywordTest {

    @Test
    @DisplayName("정상적인 키워드로 실시간 프로젝트 검색 성공 및 로깅 검증")
    void searchByKeywordSuccess() {
      // given
      String keyword = "AI";
      int size = 5;
      List<RealTimeProjectResponse> expectedResponses =
          List.of(mock(RealTimeProjectResponse.class), mock(RealTimeProjectResponse.class));

      given(searchRealTimeProjectsPort.searchByKeyword(keyword, size))
          .willReturn(expectedResponses);

      // when
      List<RealTimeProjectResponse> result = service.searchByKeyword(keyword, size);

      // then
      assertThat(result).isEqualTo(expectedResponses);

      // 포트 호출 검증
      then(searchRealTimeProjectsPort).should().searchByKeyword(keyword, size);

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(
              eq("SearchRealTimeProjectsUseCase"),
              contains("자동완성을 위한 실시간 프로젝트 목록 조회 서비스 시작 keyword=" + keyword));
      then(loggerService)
          .should()
          .logSuccess(
              eq("SearchRealTimeProjectsUseCase"),
              contains("자동완성을 위한 실시간 프로젝트 목록 조회 서비스 종료 keyword=" + keyword),
              any(Instant.class));
    }

    @Test
    @DisplayName("null 키워드에 대한 빈 결과 반환")
    void searchByKeywordWithNullKeyword() {
      // given
      String keyword = null;
      int size = 5;

      // when
      List<RealTimeProjectResponse> result = service.searchByKeyword(keyword, size);

      // then
      assertThat(result).isEmpty();

      // 포트 호출되지 않음 검증
      then(searchRealTimeProjectsPort).should(never()).searchByKeyword(anyString(), anyInt());

      // 로깅 검증 - null 키워드는 logStart만 호출되고 logSuccess는 호출되지 않음
      then(loggerService)
          .should()
          .logStart(
              eq("SearchRealTimeProjectsUseCase"),
              contains("자동완성을 위한 실시간 프로젝트 목록 조회 서비스 시작 keyword=" + keyword));
      then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
    }

    @Test
    @DisplayName("빈 문자열 키워드에 대한 빈 결과 반환")
    void searchByKeywordWithEmptyKeyword() {
      // given
      String keyword = "   ";
      int size = 5;

      // when
      List<RealTimeProjectResponse> result = service.searchByKeyword(keyword, size);

      // then
      assertThat(result).isEmpty();

      // 포트 호출되지 않음 검증
      then(searchRealTimeProjectsPort).should(never()).searchByKeyword(anyString(), anyInt());

      // 로깅 검증 - 빈 문자열 키워드는 logStart만 호출되고 logSuccess는 호출되지 않음
      then(loggerService)
          .should()
          .logStart(
              eq("SearchRealTimeProjectsUseCase"),
              contains("자동완성을 위한 실시간 프로젝트 목록 조회 서비스 시작 keyword=" + keyword));
      then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
    }

    @Test
    @DisplayName("빈 결과에 대한 처리")
    void searchByKeywordWithEmptyResult() {
      // given
      String keyword = "NonExistent";
      int size = 5;

      given(searchRealTimeProjectsPort.searchByKeyword(keyword, size)).willReturn(List.of());

      // when
      List<RealTimeProjectResponse> result = service.searchByKeyword(keyword, size);

      // then
      assertThat(result).isEmpty();

      // 포트 호출 검증
      then(searchRealTimeProjectsPort).should().searchByKeyword(keyword, size);

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(
              eq("SearchRealTimeProjectsUseCase"),
              contains("자동완성을 위한 실시간 프로젝트 목록 조회 서비스 시작 keyword=" + keyword));
      then(loggerService)
          .should()
          .logSuccess(
              eq("SearchRealTimeProjectsUseCase"),
              contains("자동완성을 위한 실시간 프로젝트 목록 조회 서비스 종료 keyword=" + keyword),
              any(Instant.class));
    }
  }

  @Nested
  @DisplayName("searchSimilarProjects 메서드 테스트")
  class SearchSimilarProjectsTest {

    @Test
    @DisplayName("유사 프로젝트 검색 성공 및 로깅 검증")
    void searchSimilarProjectsSuccess() {
      // given
      Long projectId = 100L;
      int size = 5;
      Project project = createProject(projectId, "Test Project");
      List<SimilarProjectResponse> expectedResponses =
          List.of(mock(SimilarProjectResponse.class), mock(SimilarProjectResponse.class));

      given(findProjectPort.findProjectById(projectId)).willReturn(Optional.of(project));
      given(searchSimilarProjectsPort.searchSimilarProjects(project, size))
          .willReturn(expectedResponses);

      // when
      List<SimilarProjectResponse> result = service.searchSimilarProjects(projectId, size);

      // then
      assertThat(result).isEqualTo(expectedResponses);

      // 포트 호출 검증
      then(findProjectPort).should().findProjectById(projectId);
      then(searchSimilarProjectsPort).should().searchSimilarProjects(project, size);

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(
              eq("SearchSimilarProjectsUseCase"),
              contains("유사 프로젝트 목록 조회 서비스 시작 projectId=" + projectId));
      then(loggerService)
          .should()
          .logSuccess(
              eq("SearchSimilarProjectsUseCase"),
              contains("유사 프로젝트 목록 조회 서비스 종료 projectId=" + projectId),
              any(Instant.class));
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 ID로 검색 시 예외 발생 및 로깅 검증")
    void searchSimilarProjectsWithNonExistentProject() {
      // given
      Long projectId = 999L;
      int size = 5;

      given(findProjectPort.findProjectById(projectId)).willReturn(Optional.empty());

      // when & then
      ProjectException exception =
          catchThrowableOfType(
              () -> service.searchSimilarProjects(projectId, size), ProjectException.class);
      assertAll(
          () -> assertThat(exception).isNotNull(),
          () ->
              assertThat(exception.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT));

      // 포트 호출 검증
      then(findProjectPort).should().findProjectById(projectId);
      then(searchSimilarProjectsPort)
          .should(never())
          .searchSimilarProjects(any(Project.class), anyInt());

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(
              eq("SearchSimilarProjectsUseCase"),
              contains("유사 프로젝트 목록 조회 서비스 시작 projectId=" + projectId));
      then(loggerService)
          .should()
          .logWarning(
              eq("SearchSimilarProjectsUseCase"),
              contains("해당 프로젝트가 존재하지 않습니다. projectId=" + projectId));
      then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
    }

    @Test
    @DisplayName("빈 유사 프로젝트 결과에 대한 처리")
    void searchSimilarProjectsWithEmptyResult() {
      // given
      Long projectId = 100L;
      int size = 5;
      Project project = createProject(projectId, "Test Project");

      given(findProjectPort.findProjectById(projectId)).willReturn(Optional.of(project));
      given(searchSimilarProjectsPort.searchSimilarProjects(project, size)).willReturn(List.of());

      // when
      List<SimilarProjectResponse> result = service.searchSimilarProjects(projectId, size);

      // then
      assertThat(result).isEmpty();

      // 포트 호출 검증
      then(findProjectPort).should().findProjectById(projectId);
      then(searchSimilarProjectsPort).should().searchSimilarProjects(project, size);

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(
              eq("SearchSimilarProjectsUseCase"),
              contains("유사 프로젝트 목록 조회 서비스 시작 projectId=" + projectId));
      then(loggerService)
          .should()
          .logSuccess(
              eq("SearchSimilarProjectsUseCase"),
              contains("유사 프로젝트 목록 조회 서비스 종료 projectId=" + projectId),
              any(Instant.class));
    }
  }

  @Nested
  @DisplayName("searchByFilters 메서드 테스트")
  class SearchByFiltersTest {

    @Test
    @DisplayName("필터링된 프로젝트 검색 성공 및 로깅 검증")
    void searchByFiltersSuccess() {
      // given
      FilteringProjectRequest request = new FilteringProjectRequest("AI", "LATEST", 1L, 2L, 3L, 4L);
      Pageable pageable = PageRequest.of(0, 10);

      Project project1 = createProject(1L, "Project 1");
      Project project2 = createProject(2L, "Project 2");
      Page<Project> projectPage = new PageImpl<>(List.of(project1, project2), pageable, 2);

      ProjectLabelMapResponse labelResponse = mock(ProjectLabelMapResponse.class);
      FilteredProjectResponse response1 = mock(FilteredProjectResponse.class);
      FilteredProjectResponse response2 = mock(FilteredProjectResponse.class);

      given(searchFilteredProjectsPort.searchByFilters(request, pageable, ProjectSortType.LATEST))
          .willReturn(projectPage);
      given(findProjectLabelMapUseCase.labelMapping(List.of(project1, project2)))
          .willReturn(labelResponse);
      given(labelResponse.usernameMap()).willReturn(Map.of(100L, "user1"));
      given(labelResponse.userProfileUrlMap()).willReturn(Map.of(100L, "profile1.jpg"));
      given(labelResponse.topicLabelMap()).willReturn(Map.of(1L, "AI"));
      given(labelResponse.analysisPurposeLabelMap()).willReturn(Map.of(2L, "Research"));
      given(labelResponse.dataSourceLabelMap()).willReturn(Map.of(3L, "Public"));
      given(labelResponse.authorLevelLabelMap()).willReturn(Map.of(4L, "Expert"));

      given(findUsernameUseCase.findUsernamesByIds(List.of())).willReturn(Map.of());
      given(findUserThumbnailUseCase.findUserThumbnailsByIds(List.of())).willReturn(Map.of());

      given(
              filteredProjectDtoMapper.toResponseDto(
                  eq(project1),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyMap(),
                  anyMap()))
          .willReturn(response1);
      given(
              filteredProjectDtoMapper.toResponseDto(
                  eq(project2),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyString(),
                  anyMap(),
                  anyMap()))
          .willReturn(response2);

      // when
      Page<FilteredProjectResponse> result = service.searchByFilters(request, pageable);

      // then
      assertAll(
          () -> assertThat(result).isNotNull(),
          () -> assertThat(result.getContent()).hasSize(2),
          () -> assertThat(result.getContent()).containsExactly(response1, response2));

      // 포트 호출 검증
      then(searchFilteredProjectsPort)
          .should()
          .searchByFilters(request, pageable, ProjectSortType.LATEST);
      then(findProjectLabelMapUseCase).should().labelMapping(List.of(project1, project2));

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(
              eq("SearchFilteredProjectsUseCase"),
              contains("필터링된 프로젝트 목록 조회 서비스 시작 keyword=" + request.keyword()));
      then(loggerService)
          .should()
          .logSuccess(
              eq("SearchFilteredProjectsUseCase"),
              contains("필터링된 프로젝트 목록 조회 서비스 종료 keyword=" + request.keyword()),
              any(Instant.class));
    }

    @Test
    @DisplayName("null 정렬 타입에 대한 기본 처리")
    void searchByFiltersWithNullSortType() {
      // given
      FilteringProjectRequest request = new FilteringProjectRequest("AI", null, 1L, 2L, 3L, 4L);
      Pageable pageable = PageRequest.of(0, 10);

      Page<Project> emptyPage = new PageImpl<>(List.of(), pageable, 0);

      given(searchFilteredProjectsPort.searchByFilters(request, pageable, null))
          .willReturn(emptyPage);
      given(findProjectLabelMapUseCase.labelMapping(List.of()))
          .willReturn(mock(ProjectLabelMapResponse.class));

      // when
      Page<FilteredProjectResponse> result = service.searchByFilters(request, pageable);

      // then
      assertAll(
          () -> assertThat(result).isNotNull(), () -> assertThat(result.getContent()).isEmpty());

      // 포트 호출 검증
      then(searchFilteredProjectsPort).should().searchByFilters(request, pageable, null);
      then(findProjectLabelMapUseCase).should().labelMapping(List.of());

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(
              eq("SearchFilteredProjectsUseCase"),
              contains("필터링된 프로젝트 목록 조회 서비스 시작 keyword=" + request.keyword()));
      then(loggerService)
          .should()
          .logSuccess(
              eq("SearchFilteredProjectsUseCase"),
              contains("필터링된 프로젝트 목록 조회 서비스 종료 keyword=" + request.keyword()),
              any(Instant.class));
    }

    @Test
    @DisplayName("빈 정렬 타입에 대한 기본 처리")
    void searchByFiltersWithEmptySortType() {
      // given
      FilteringProjectRequest request = new FilteringProjectRequest("AI", "", 1L, 2L, 3L, 4L);
      Pageable pageable = PageRequest.of(0, 10);

      Page<Project> emptyPage = new PageImpl<>(List.of(), pageable, 0);

      given(searchFilteredProjectsPort.searchByFilters(request, pageable, null))
          .willReturn(emptyPage);
      given(findProjectLabelMapUseCase.labelMapping(List.of()))
          .willReturn(mock(ProjectLabelMapResponse.class));

      // when
      Page<FilteredProjectResponse> result = service.searchByFilters(request, pageable);

      // then
      assertAll(
          () -> assertThat(result).isNotNull(), () -> assertThat(result.getContent()).isEmpty());

      // 포트 호출 검증
      then(searchFilteredProjectsPort).should().searchByFilters(request, pageable, null);
      then(findProjectLabelMapUseCase).should().labelMapping(List.of());

      // 로깅 검증
      then(loggerService)
          .should()
          .logStart(
              eq("SearchFilteredProjectsUseCase"),
              contains("필터링된 프로젝트 목록 조회 서비스 시작 keyword=" + request.keyword()));
      then(loggerService)
          .should()
          .logSuccess(
              eq("SearchFilteredProjectsUseCase"),
              contains("필터링된 프로젝트 목록 조회 서비스 종료 keyword=" + request.keyword()),
              any(Instant.class));
    }
  }

  private Project createProject(Long id, String title) {
    return Project.of(
        id,
        title,
        1L,
        100L,
        2L,
        3L,
        4L,
        false,
        null,
        "Content",
        "thumbnail.jpg",
        List.of(),
        LocalDateTime.now(),
        0L,
        0L,
        0L,
        false,
        List.of());
  }
}
