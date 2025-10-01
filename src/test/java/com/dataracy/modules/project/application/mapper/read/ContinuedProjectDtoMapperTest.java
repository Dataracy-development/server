package com.dataracy.modules.project.application.mapper.read;

import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ContinuedProjectDtoMapperTest {

    private final ContinuedProjectDtoMapper mapper = new ContinuedProjectDtoMapper();

    @Test
    @DisplayName("성공 → Project와 사용자 및 라벨 정보를 ContinuedProjectResponse로 매핑")
    void toResponseDtoSuccess() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2025, 8, 27, 15, 0);

        Project project = Project.of(
                1L,                 // id
                "proj-title",       // title
                10L,                // topicId
                99L,                // userId
                2L,                 // analysisPurposeId
                3L,                 // dataSourceId
                4L,                 // authorLevelId
                true,               // isContinue
                null,               // parentProjectId
                "content",          // content
                "thumb.png",        // thumbnailUrl
                List.of(100L, 200L),// dataIds
                createdAt,          // createdAt
                5L,                 // commentCount
                6L,                 // likeCount
                7L,                 // viewCount
                false,              // isDeleted
                List.of()           // childProjects
        );

        String username = "tester";
        String userProfileUrl = "profile.png";
        String topicLabel = "topic-label";
        String authorLevelLabel = "level-label";

        // when
        ContinuedProjectResponse response = mapper.toResponseDto(
                project, username, userProfileUrl, topicLabel, authorLevelLabel
        );

        // then
        assertAll(
                () -> assertThat(response.id()).isEqualTo(1L),
                () -> assertThat(response.title()).isEqualTo("proj-title"),
                () -> assertThat(response.creatorName()).isEqualTo("tester"),
                () -> assertThat(response.userProfileImageUrl()).isEqualTo("profile.png"),
                () -> assertThat(response.projectThumbnailUrl()).isEqualTo("thumb.png"),
                () -> assertThat(response.topicLabel()).isEqualTo("topic-label"),
                () -> assertThat(response.authorLevelLabel()).isEqualTo("level-label"),
                () -> assertThat(response.commentCount()).isEqualTo(5L),
                () -> assertThat(response.likeCount()).isEqualTo(6L),
                () -> assertThat(response.viewCount()).isEqualTo(7L),
                () -> assertThat(response.createdAt()).isEqualTo(createdAt)
        );
    }
}

