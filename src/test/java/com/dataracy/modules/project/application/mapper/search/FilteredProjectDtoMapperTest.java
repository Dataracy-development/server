package com.dataracy.modules.project.application.mapper.search;

import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FilteredProjectDtoMapperTest {

    private final FilteredProjectDtoMapper mapper = new FilteredProjectDtoMapper();

    @Test
    @DisplayName("성공 → Project와 메타데이터를 FilteredProjectResponse로 매핑 (자식 사용자명 매핑 포함)")
    void toResponseDtoSuccessWithChildUsernames() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2025, 8, 27, 20, 0);

        Project child1 = Project.of(
                101L, "child-1", 1L, 201L, 2L, 3L, 4L,
                true, null, "child-content-1", "thumb1.png",
                List.of(), createdAt, 1L, 2L, 3L, false, List.of()
        );

        Project child2 = Project.of(
                102L, "child-2", 1L, 202L, 2L, 3L, 4L,
                true, null, "child-content-2", "thumb2.png",
                List.of(), createdAt, 4L, 5L, 6L, false, List.of()
        );

        Project parent = Project.of(
                1L, "parent-title", 10L, 200L, 2L, 3L, 4L,
                true, null, "parent-content", "parent-thumb.png",
                List.of(100L, 200L), createdAt, 7L, 8L, 9L,
                false, List.of(child1, child2)
        );

        Map<Long, String> childUsernames = Map.of(
                201L, "child-user-1"
        );

        Map<Long, String> childUserProfileUrls = Map.of(
                201L, "https://~~"
        );

        // when
        FilteredProjectResponse response = mapper.toResponseDto(
                parent,
                "parent-user",
                "https://~~",
                "topic-label",
                "analysis-purpose-label",
                "data-source-label",
                "author-level-label",
                childUsernames,
                childUserProfileUrls
        );

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("parent-title");
        assertThat(response.content()).isEqualTo("parent-content");
        assertThat(response.creatorName()).isEqualTo("parent-user");
        assertThat(response.projectThumbnailUrl()).isEqualTo("parent-thumb.png");
        assertThat(response.topicLabel()).isEqualTo("topic-label");
        assertThat(response.analysisPurposeLabel()).isEqualTo("analysis-purpose-label");
        assertThat(response.dataSourceLabel()).isEqualTo("data-source-label");
        assertThat(response.authorLevelLabel()).isEqualTo("author-level-label");
        assertThat(response.commentCount()).isEqualTo(7L);
        assertThat(response.likeCount()).isEqualTo(8L);
        assertThat(response.viewCount()).isEqualTo(9L);
        assertThat(response.createdAt()).isEqualTo(createdAt);

        // 자식 프로젝트 매핑 확인
        assertThat(response.childProjects()).hasSize(2);

        ChildProjectResponse mappedChild1 = response.childProjects().get(0);
        assertThat(mappedChild1.id()).isEqualTo(101L);
        assertThat(mappedChild1.title()).isEqualTo("child-1");
        assertThat(mappedChild1.creatorName()).isEqualTo("child-user-1");

        ChildProjectResponse mappedChild2 = response.childProjects().get(1);
        assertThat(mappedChild2.id()).isEqualTo(102L);
        assertThat(mappedChild2.title()).isEqualTo("child-2");
        assertThat(mappedChild2.creatorName()).isEqualTo("익명 유저"); // childUsernames에 없음 → 대체됨
    }
}
