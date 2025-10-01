package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import com.dataracy.modules.project.application.mapper.read.UserProjectDtoMapper;
import com.dataracy.modules.project.application.port.out.query.read.FindUserProjectsPort;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserProjectReadServiceTest {

    @Mock
    private UserProjectDtoMapper userProjectDtoMapper;

    @Mock
    private FindUserProjectsPort findUserProjectsPort;

    @Mock
    private GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;

    @Mock
    private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    @InjectMocks
    private UserProjectReadService service;

    private MockedStatic<LoggerFactory> loggerFactoryMock;
    private com.dataracy.modules.common.logging.ServiceLogger loggerService;

    @BeforeEach
    void setUp() {
        loggerFactoryMock = mockStatic(LoggerFactory.class);
        loggerService = mock(com.dataracy.modules.common.logging.ServiceLogger.class);
        loggerFactoryMock.when(LoggerFactory::service).thenReturn(loggerService);
        doReturn(Instant.now()).when(loggerService).logStart(anyString(), anyString());
        doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Nested
    @DisplayName("findUserProjects 메서드 테스트")
    class FindUserProjectsTest {

        @Test
        @DisplayName("사용자 프로젝트 조회 성공 및 로깅 검증")
        void findUserProjectsSuccess() {
            // given
            Long userId = 100L;
            Pageable pageable = PageRequest.of(0, 10);

            List<Project> projects = List.of(
                    createProject(1L, "Project 1", 1L, userId, 2L),
                    createProject(2L, "Project 2", 3L, userId, 4L)
            );
            Page<Project> projectPage = new PageImpl<>(projects, pageable, 2);

            Map<Long, String> topicLabelMap = Map.of(1L, "AI", 3L, "Data Science");
            Map<Long, String> authorLevelLabelMap = Map.of(2L, "Expert", 4L, "Beginner");

            UserProjectResponse response1 = mock(UserProjectResponse.class);
            UserProjectResponse response2 = mock(UserProjectResponse.class);

            given(findUserProjectsPort.findUserProjects(userId, pageable)).willReturn(projectPage);
            given(getTopicLabelFromIdUseCase.getLabelsByIds(List.of(1L, 3L))).willReturn(topicLabelMap);
            given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(List.of(2L, 4L))).willReturn(authorLevelLabelMap);
            given(userProjectDtoMapper.toResponseDto(projects.get(0), "AI", "Expert")).willReturn(response1);
            given(userProjectDtoMapper.toResponseDto(projects.get(1), "Data Science", "Beginner")).willReturn(response2);

            // when
            Page<UserProjectResponse> result = service.findUserProjects(userId, pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).hasSize(2),
                    () -> assertThat(result.getContent()).containsExactly(response1, response2)
            );

            // 포트 호출 검증
            then(findUserProjectsPort).should().findUserProjects(userId, pageable);
            then(getTopicLabelFromIdUseCase).should().getLabelsByIds(List.of(1L, 3L));
            then(getAuthorLevelLabelFromIdUseCase).should().getLabelsByIds(List.of(2L, 4L));

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindUserProjectsUseCase"),
                    eq("해당 회원이 작성한 프로젝트 목록 조회 서비스 시작 userId=" + userId));
            then(loggerService).should().logSuccess(eq("FindUserProjectsUseCase"),
                    eq("해당 회원이 작성한 프로젝트 목록 조회 서비스 종료 userId=" + userId), any(Instant.class));
        }

        @Test
        @DisplayName("빈 결과에 대한 사용자 프로젝트 조회")
        void findUserProjectsWithEmptyResult() {
            // given
            Long userId = 100L;
            Pageable pageable = PageRequest.of(0, 10);
            Page<Project> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(findUserProjectsPort.findUserProjects(userId, pageable)).willReturn(emptyPage);
            given(getTopicLabelFromIdUseCase.getLabelsByIds(List.of())).willReturn(Map.of());
            given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(List.of())).willReturn(Map.of());

            // when
            Page<UserProjectResponse> result = service.findUserProjects(userId, pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).isEmpty(),
                    () -> assertThat(result.getTotalElements()).isZero()
            );

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindUserProjectsUseCase"),
                    eq("해당 회원이 작성한 프로젝트 목록 조회 서비스 시작 userId=" + userId));
            then(loggerService).should().logSuccess(eq("FindUserProjectsUseCase"),
                    eq("해당 회원이 작성한 프로젝트 목록 조회 서비스 종료 userId=" + userId), any(Instant.class));
        }
    }

    @Nested
    @DisplayName("findLikeProjects 메서드 테스트")
    class FindLikeProjectsTest {

        @Test
        @DisplayName("사용자가 좋아요한 프로젝트 조회 성공 및 로깅 검증")
        void findLikeProjectsSuccess() {
            // given
            Long userId = 200L;
            Pageable pageable = PageRequest.of(0, 5);

            List<Project> projects = List.of(
                    createProject(10L, "Liked Project 1", 5L, 300L, 6L),
                    createProject(11L, "Liked Project 2", 7L, 400L, 8L)
            );
            Page<Project> projectPage = new PageImpl<>(projects, pageable, 2);

            Map<Long, String> topicLabelMap = Map.of(5L, "Machine Learning", 7L, "Deep Learning");
            Map<Long, String> authorLevelLabelMap = Map.of(6L, "Advanced", 8L, "Intermediate");

            UserProjectResponse response1 = mock(UserProjectResponse.class);
            UserProjectResponse response2 = mock(UserProjectResponse.class);

            given(findUserProjectsPort.findLikeProjects(userId, pageable)).willReturn(projectPage);
            given(getTopicLabelFromIdUseCase.getLabelsByIds(List.of(5L, 7L))).willReturn(topicLabelMap);
            given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(List.of(6L, 8L))).willReturn(authorLevelLabelMap);
            given(userProjectDtoMapper.toResponseDto(projects.get(0), "Machine Learning", "Advanced")).willReturn(response1);
            given(userProjectDtoMapper.toResponseDto(projects.get(1), "Deep Learning", "Intermediate")).willReturn(response2);

            // when
            Page<UserProjectResponse> result = service.findLikeProjects(userId, pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).hasSize(2),
                    () -> assertThat(result.getContent()).containsExactly(response1, response2)
            );

            // 포트 호출 검증
            then(findUserProjectsPort).should().findLikeProjects(userId, pageable);
            then(getTopicLabelFromIdUseCase).should().getLabelsByIds(List.of(5L, 7L));
            then(getAuthorLevelLabelFromIdUseCase).should().getLabelsByIds(List.of(6L, 8L));

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindLikeProjectsUseCase"),
                    eq("해당 회원이 좋아요한 프로젝트 목록 조회 서비스 시작 userId=" + userId));
            then(loggerService).should().logSuccess(eq("FindLikeProjectsUseCase"),
                    eq("해당 회원이 좋아요한 프로젝트 목록 조회 서비스 종료 userId=" + userId), any(Instant.class));
        }

        @Test
        @DisplayName("좋아요한 프로젝트가 없는 경우")
        void findLikeProjectsWithEmptyResult() {
            // given
            Long userId = 200L;
            Pageable pageable = PageRequest.of(0, 5);
            Page<Project> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(findUserProjectsPort.findLikeProjects(userId, pageable)).willReturn(emptyPage);
            given(getTopicLabelFromIdUseCase.getLabelsByIds(List.of())).willReturn(Map.of());
            given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(List.of())).willReturn(Map.of());

            // when
            Page<UserProjectResponse> result = service.findLikeProjects(userId, pageable);

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getContent()).isEmpty(),
                    () -> assertThat(result.getTotalElements()).isZero()
            );

            // 로깅 검증
            then(loggerService).should().logStart(eq("FindLikeProjectsUseCase"),
                    eq("해당 회원이 좋아요한 프로젝트 목록 조회 서비스 시작 userId=" + userId));
            then(loggerService).should().logSuccess(eq("FindLikeProjectsUseCase"),
                    eq("해당 회원이 좋아요한 프로젝트 목록 조회 서비스 종료 userId=" + userId), any(Instant.class));
        }
    }

    private Project createProject(Long id, String title, Long topicId, Long userId, Long authorLevelId) {
        return Project.of(
                id, title, topicId, userId, 1L, 2L, authorLevelId,
                false, null, "Content", "thumbnail.jpg",
                List.of(), LocalDateTime.now(),
                0L, 0L, 0L, false, List.of()
        );
    }
}