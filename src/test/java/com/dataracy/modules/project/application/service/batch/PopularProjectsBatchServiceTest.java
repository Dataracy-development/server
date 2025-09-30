package com.dataracy.modules.project.application.service.batch;

import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.application.mapper.read.PopularProjectDtoMapper;
import com.dataracy.modules.project.application.port.in.query.extractor.FindProjectLabelMapUseCase;
import com.dataracy.modules.project.application.port.out.query.read.GetPopularProjectsPort;
import com.dataracy.modules.project.application.port.out.storage.PopularProjectsStoragePort;
import com.dataracy.modules.project.domain.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
class PopularProjectsBatchServiceTest {

    @Mock
    private PopularProjectDtoMapper popularProjectDtoMapper;

    @Mock
    private FindProjectLabelMapUseCase findProjectLabelMapUseCase;

    @Mock
    private PopularProjectsStoragePort popularProjectsStoragePort;

    @Mock
    private GetPopularProjectsPort getPopularProjectsPort;

    @InjectMocks
    private PopularProjectsBatchService popularProjectsBatchService;

    private Project createSampleProject(Long id, Long userId, Long topicId, Long analysisPurposeId, Long dataSourceId, Long authorLevelId) {
        return Project.builder()
                .id(id)
                .userId(userId)
                .topicId(topicId)
                .analysisPurposeId(analysisPurposeId)
                .dataSourceId(dataSourceId)
                .authorLevelId(authorLevelId)
                .title("Test Project")
                .content("Test Content")
                .createdAt(LocalDateTime.now())
                .commentCount(5L)
                .likeCount(10L)
                .viewCount(15L)
                .build();
    }

    @Nested
    @DisplayName("인기 프로젝트 캐시 업데이트")
    class UpdatePopularProjectsCache {

        @Test
        @DisplayName("스케줄된 캐시 업데이트 성공")
        void updatePopularProjectsCacheSuccess() {
            // given
            Project project1 = createSampleProject(1L, 100L, 10L, 20L, 30L, 40L);
            Project project2 = createSampleProject(2L, 200L, 11L, 21L, 31L, 41L);
            List<Project> savedProjects = List.of(project1, project2);

            ProjectLabelMapResponse labelResponse = new ProjectLabelMapResponse(
                    Map.of(100L, "user1", 200L, "user2"),
                    Map.of(100L, "profile1.png", 200L, "profile2.png"),
                    Map.of(10L, "topic1", 11L, "topic2"),
                    Map.of(20L, "purpose1", 21L, "purpose2"),
                    Map.of(30L, "source1", 31L, "source2"),
                    Map.of(40L, "level1", 41L, "level2")
            );

            PopularProjectResponse response1 = new PopularProjectResponse(
                    1L, "Test Project", "Test Content", 100L, "user1", "profile1.png", "thumbnail.png",
                    "topic1", "purpose1", "source1", "level1", 5L, 10L, 15L
            );
            PopularProjectResponse response2 = new PopularProjectResponse(
                    2L, "Test Project", "Test Content", 200L, "user2", "profile2.png", "thumbnail.png",
                    "topic2", "purpose2", "source2", "level2", 5L, 10L, 15L
            );

            given(getPopularProjectsPort.getPopularProjects(20)).willReturn(savedProjects);
            given(findProjectLabelMapUseCase.labelMapping(savedProjects)).willReturn(labelResponse);
            given(popularProjectDtoMapper.toResponseDto(
                    project1, "user1", "profile1.png", "topic1", "purpose1", "source1", "level1"
            )).willReturn(response1);
            given(popularProjectDtoMapper.toResponseDto(
                    project2, "user2", "profile2.png", "topic2", "purpose2", "source2", "level2"
            )).willReturn(response2);

            // when
            popularProjectsBatchService.updatePopularProjectsCache();

            // then
            then(getPopularProjectsPort).should().getPopularProjects(20);
            then(findProjectLabelMapUseCase).should().labelMapping(savedProjects);
            then(popularProjectsStoragePort).should().setPopularProjects(List.of(response1, response2));
        }

        @Test
        @DisplayName("스케줄된 캐시 업데이트 실패 - 예외 발생")
        void updatePopularProjectsCacheFailure() {
            // given
            willThrow(new RuntimeException("Database error"))
                    .given(getPopularProjectsPort).getPopularProjects(20);

            // when
            popularProjectsBatchService.updatePopularProjectsCache();

            // then
            then(getPopularProjectsPort).should().getPopularProjects(20);
            then(popularProjectsStoragePort).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("수동 캐시 업데이트")
    class ManualUpdatePopularProjectsCache {

        @Test
        @DisplayName("수동 캐시 업데이트 성공")
        void manualUpdatePopularProjectsCacheSuccess() {
            // given
            int size = 10;
            Project project = createSampleProject(1L, 100L, 10L, 20L, 30L, 40L);
            List<Project> savedProjects = List.of(project);

            ProjectLabelMapResponse labelResponse = new ProjectLabelMapResponse(
                    Map.of(100L, "user1"),
                    Map.of(100L, "profile1.png"),
                    Map.of(10L, "topic1"),
                    Map.of(20L, "purpose1"),
                    Map.of(30L, "source1"),
                    Map.of(40L, "level1")
            );

            PopularProjectResponse response = new PopularProjectResponse(
                    1L, "Test Project", "Test Content", 100L, "user1", "profile1.png", "thumbnail.png",
                    "topic1", "purpose1", "source1", "level1", 5L, 10L, 15L
            );

            given(getPopularProjectsPort.getPopularProjects(size)).willReturn(savedProjects);
            given(findProjectLabelMapUseCase.labelMapping(savedProjects)).willReturn(labelResponse);
            given(popularProjectDtoMapper.toResponseDto(
                    project, "user1", "profile1.png", "topic1", "purpose1", "source1", "level1"
            )).willReturn(response);

            // when
            popularProjectsBatchService.manualUpdatePopularProjectsCache(size);

            // then
            then(getPopularProjectsPort).should().getPopularProjects(size);
            then(findProjectLabelMapUseCase).should().labelMapping(savedProjects);
            then(popularProjectsStoragePort).should().setPopularProjects(List.of(response));
        }

        @Test
        @DisplayName("수동 캐시 업데이트 실패 - 예외 발생")
        void manualUpdatePopularProjectsCacheFailure() {
            // given
            int size = 5;
            willThrow(new RuntimeException("Database connection failed"))
                    .given(getPopularProjectsPort).getPopularProjects(size);

            // when
            popularProjectsBatchService.manualUpdatePopularProjectsCache(size);

            // then
            then(getPopularProjectsPort).should().getPopularProjects(size);
            then(popularProjectsStoragePort).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("캐시 워밍업")
    class WarmUpCacheIfNeeded {

        @Test
        @DisplayName("캐시가 유효하지 않을 때 워밍업 실행")
        void warmUpCacheIfNeededWhenCacheInvalid() {
            // given
            int size = 15;
            given(popularProjectsStoragePort.hasValidData()).willReturn(false);
            
            Project project = createSampleProject(1L, 100L, 10L, 20L, 30L, 40L);
            List<Project> savedProjects = List.of(project);

            ProjectLabelMapResponse labelResponse = new ProjectLabelMapResponse(
                    Map.of(100L, "user1"),
                    Map.of(100L, "profile1.png"),
                    Map.of(10L, "topic1"),
                    Map.of(20L, "purpose1"),
                    Map.of(30L, "source1"),
                    Map.of(40L, "level1")
            );

            PopularProjectResponse response = new PopularProjectResponse(
                    1L, "Test Project", "Test Content", 100L, "user1", "profile1.png", "thumbnail.png",
                    "topic1", "purpose1", "source1", "level1", 5L, 10L, 15L
            );

            given(getPopularProjectsPort.getPopularProjects(size)).willReturn(savedProjects);
            given(findProjectLabelMapUseCase.labelMapping(savedProjects)).willReturn(labelResponse);
            given(popularProjectDtoMapper.toResponseDto(
                    project, "user1", "profile1.png", "topic1", "purpose1", "source1", "level1"
            )).willReturn(response);

            // when
            popularProjectsBatchService.warmUpCacheIfNeeded(size);

            // then
            then(popularProjectsStoragePort).should().hasValidData();
            then(getPopularProjectsPort).should().getPopularProjects(size);
            then(popularProjectsStoragePort).should().setPopularProjects(List.of(response));
        }

        @Test
        @DisplayName("캐시가 유효할 때 워밍업 실행하지 않음")
        void warmUpCacheIfNeededWhenCacheValid() {
            // given
            int size = 15;
            given(popularProjectsStoragePort.hasValidData()).willReturn(true);

            // when
            popularProjectsBatchService.warmUpCacheIfNeeded(size);

            // then
            then(popularProjectsStoragePort).should().hasValidData();
            then(getPopularProjectsPort).shouldHaveNoInteractions();
            then(findProjectLabelMapUseCase).shouldHaveNoInteractions();
            then(popularProjectDtoMapper).shouldHaveNoInteractions();
            then(popularProjectsStoragePort).shouldHaveNoMoreInteractions();
        }
    }
}
