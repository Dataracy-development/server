package com.dataracy.modules.project.application.service.query;

import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.reference.application.port.in.analysispurpose.GetAnalysisPurposeLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.datasource.GetDataSourceLabelFromIdUseCase;
import com.dataracy.modules.reference.application.port.in.topic.GetTopicLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ProjectLabelMapServiceTest {

    @Mock
    private FindUsernameUseCase findUsernameUseCase;

    @Mock
    private FindUserThumbnailUseCase findUserThumbnailUseCase;

    @Mock
    private GetTopicLabelFromIdUseCase getTopicLabelFromIdUseCase;

    @Mock
    private GetAnalysisPurposeLabelFromIdUseCase getAnalysisPurposeLabelFromIdUseCase;

    @Mock
    private GetDataSourceLabelFromIdUseCase getDataSourceLabelFromIdUseCase;

    @Mock
    private GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    @InjectMocks
    private ProjectLabelMapService service;

    private List<Project> testProjects;

    @BeforeEach
    void setUp() {
        // 테스트용 프로젝트 생성
        testProjects = List.of(
                Project.of(
                        1L, "Test Project 1", 1L, 100L, 2L, 3L, 4L,
                        false, null, "Content 1", "thumbnail1.jpg",
                        List.of(10L, 11L), LocalDateTime.now(),
                        5L, 10L, 100L, false, List.of()
                ),
                Project.of(
                        2L, "Test Project 2", 5L, 200L, 6L, 7L, 8L,
                        true, 1L, "Content 2", "thumbnail2.jpg",
                        List.of(20L, 21L), LocalDateTime.now(),
                        3L, 5L, 50L, false, List.of()
                )
        );
    }

    @Nested
    @DisplayName("labelMapping 메서드 테스트")
    class LabelMappingTest {

        @Test
        @DisplayName("정상적인 프로젝트 컬렉션에 대한 라벨 매핑 성공")
        void labelMappingSuccess() {
            // given
            Map<Long, String> usernameMap = Map.of(100L, "user1", 200L, "user2");
            Map<Long, String> thumbnailMap = Map.of(100L, "thumb1.jpg", 200L, "thumb2.jpg");
            Map<Long, String> topicMap = Map.of(1L, "AI", 5L, "Data Science");
            Map<Long, String> analysisPurposeMap = Map.of(2L, "Research", 6L, "Analysis");
            Map<Long, String> dataSourceMap = Map.of(3L, "Public", 7L, "Private");
            Map<Long, String> authorLevelMap = Map.of(4L, "Expert", 8L, "Beginner");

            given(findUsernameUseCase.findUsernamesByIds(List.of(100L, 200L))).willReturn(usernameMap);
            given(findUserThumbnailUseCase.findUserThumbnailsByIds(List.of(100L, 200L))).willReturn(thumbnailMap);
            given(getTopicLabelFromIdUseCase.getLabelsByIds(List.of(1L, 5L))).willReturn(topicMap);
            given(getAnalysisPurposeLabelFromIdUseCase.getLabelsByIds(List.of(2L, 6L))).willReturn(analysisPurposeMap);
            given(getDataSourceLabelFromIdUseCase.getLabelsByIds(List.of(3L, 7L))).willReturn(dataSourceMap);
            given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(List.of(4L, 8L))).willReturn(authorLevelMap);

            // when
            ProjectLabelMapResponse result = service.labelMapping(testProjects);

            // then
            assertThat(result).isNotNull();
            assertThat(result.usernameMap()).isEqualTo(usernameMap);
            assertThat(result.userProfileUrlMap()).isEqualTo(thumbnailMap);
            assertThat(result.topicLabelMap()).isEqualTo(topicMap);
            assertThat(result.analysisPurposeLabelMap()).isEqualTo(analysisPurposeMap);
            assertThat(result.dataSourceLabelMap()).isEqualTo(dataSourceMap);
            assertThat(result.authorLevelLabelMap()).isEqualTo(authorLevelMap);
        }

        @Test
        @DisplayName("빈 프로젝트 컬렉션에 대한 라벨 매핑")
        void labelMappingWithEmptyCollection() {
            // given
            Collection<Project> emptyProjects = List.of();
            Map<Long, String> emptyMap = Map.of();

            given(findUsernameUseCase.findUsernamesByIds(List.of())).willReturn(emptyMap);
            given(findUserThumbnailUseCase.findUserThumbnailsByIds(List.of())).willReturn(emptyMap);
            given(getTopicLabelFromIdUseCase.getLabelsByIds(List.of())).willReturn(emptyMap);
            given(getAnalysisPurposeLabelFromIdUseCase.getLabelsByIds(List.of())).willReturn(emptyMap);
            given(getDataSourceLabelFromIdUseCase.getLabelsByIds(List.of())).willReturn(emptyMap);
            given(getAuthorLevelLabelFromIdUseCase.getLabelsByIds(List.of())).willReturn(emptyMap);

            // when
            ProjectLabelMapResponse result = service.labelMapping(emptyProjects);

            // then
            assertThat(result).isNotNull();
            assertThat(result.usernameMap()).isEmpty();
            assertThat(result.userProfileUrlMap()).isEmpty();
            assertThat(result.topicLabelMap()).isEmpty();
            assertThat(result.analysisPurposeLabelMap()).isEmpty();
            assertThat(result.dataSourceLabelMap()).isEmpty();
            assertThat(result.authorLevelLabelMap()).isEmpty();
        }

    }
}