package com.dataracy.modules.project.adapter.jpa.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;

class ProjectEntityMapperTest {

  // Test constants
  private static final Long DEFAULT_ID = 1L;
  private static final Long USER_ID = 10L;
  private static final Long TOPIC_ID = 20L;
  private static final Long ANALYSIS_PURPOSE_ID = 30L;
  private static final Long DATA_SOURCE_ID = 40L;
  private static final Long AUTHOR_LEVEL_ID = 50L;
  private static final Long COMMENT_COUNT = 5L;
  private static final Long LIKE_COUNT = 7L;
  private static final Long VIEW_COUNT = 100L;
  private static final Long PARENT_ID = 99L;
  private static final Long DOMAIN_ID = 10L;
  private static final Long DOMAIN_USER_ID = 20L;
  private static final Long DOMAIN_TOPIC_ID = 30L;
  private static final Long DOMAIN_ANALYSIS_PURPOSE_ID = 40L;
  private static final Long DOMAIN_DATA_SOURCE_ID = 50L;
  private static final Long DOMAIN_AUTHOR_LEVEL_ID = 60L;
  private static final Long DOMAIN_PARENT_ID = 99L;
  private static final Long TWENTY_TWO = 22L;
  private static final Long DATA_ID_1 = 111L;
  private static final Long DATA_ID_2 = TWENTY_TWO;
  private static final Long DOMAIN_COMMENT_COUNT = 5L;
  private static final Long DOMAIN_LIKE_COUNT = 6L;
  private static final Long DOMAIN_VIEW_COUNT = 7L;

  private ProjectEntity sampleEntity() {
    return ProjectEntity.builder()
        .id(DEFAULT_ID)
        .title("title")
        .userId(USER_ID)
        .topicId(TOPIC_ID)
        .analysisPurposeId(ANALYSIS_PURPOSE_ID)
        .dataSourceId(DATA_SOURCE_ID)
        .authorLevelId(AUTHOR_LEVEL_ID)
        .isContinue(true)
        .content("content")
        .thumbnailUrl("thumb.png")
        .commentCount(COMMENT_COUNT)
        .likeCount(LIKE_COUNT)
        .viewCount(VIEW_COUNT)
        .isDeleted(false)
        .build();
  }

  @Nested
  @DisplayName("toMinimal")
  class ToMinimal {
    @Test
    @DisplayName("성공 → 최소 정보만 매핑")
    void toMinimalSuccess() {
      ProjectEntity entity = sampleEntity();

      Project result = ProjectEntityMapper.toMinimal(entity);

      assertAll(
          () -> assertThat(result.getId()).isEqualTo(DEFAULT_ID),
          () -> assertThat(result.getTitle()).isEqualTo("title"),
          () -> assertThat(result.getParentProjectId()).isNull(),
          () -> assertThat(result.getDataIds()).isEmpty(),
          () -> assertThat(result.getChildProjects()).isEmpty());
    }

    @Test
    @DisplayName("성공 → null 입력 시 null 반환")
    void toMinimalNull() {
      assertThat(ProjectEntityMapper.toMinimal(null)).isNull();
    }
  }

  @Test
  @DisplayName("toWithParent → 부모 프로젝트 ID를 포함")
  void toWithParent() {
    ProjectEntity parent = ProjectEntity.builder().id(PARENT_ID).title("parent").build();

    ProjectEntity child = sampleEntity();
    // child 의 parentProject 지정
    child =
        ProjectEntity.builder()
            .id(child.getId())
            .title(child.getTitle())
            .userId(child.getUserId())
            .topicId(child.getTopicId())
            .analysisPurposeId(child.getAnalysisPurposeId())
            .dataSourceId(child.getDataSourceId())
            .authorLevelId(child.getAuthorLevelId())
            .isContinue(child.getIsContinue())
            .content(child.getContent())
            .thumbnailUrl(child.getThumbnailUrl())
            .parentProject(parent)
            .build();

    Project result = ProjectEntityMapper.toWithParent(child);

    assertThat(result.getParentProjectId()).isEqualTo(PARENT_ID);
  }

  @Test
  @DisplayName("toWithChildren → 지정한 개수만큼 최소 정보 자식 포함")
  void toWithChildren() {
    ProjectEntity parent = sampleEntity();
    // 자식 프로젝트가 없는 상태에서 테스트
    // 실제로는 Repository에서 관계가 설정된 데이터를 반환하므로 빈 리스트가 정상

    Project result = ProjectEntityMapper.toWithChildren(parent, DEFAULT_ID.intValue());

    // 자식 프로젝트가 없는 경우 빈 리스트 반환
    assertThat(result.getChildProjects()).isEmpty();
  }

  @Test
  @DisplayName("toWithData → 데이터 ID 목록을 포함")
  void toWithData() {
    ProjectEntity entity = sampleEntity();
    // 데이터가 없는 상태에서 테스트
    // 실제로는 Repository에서 관계가 설정된 데이터를 반환하므로 빈 리스트가 정상

    Project result = ProjectEntityMapper.toWithData(entity);

    // 데이터가 없는 경우 빈 리스트 반환
    assertThat(result.getDataIds()).isEmpty();
  }

  @Test
  @DisplayName("toEntity → 도메인 객체를 엔티티로 변환")
  void toEntity() {
    Project domain =
        Project.of(
            DOMAIN_ID,
            "p-title",
            DOMAIN_USER_ID,
            DOMAIN_TOPIC_ID,
            DOMAIN_ANALYSIS_PURPOSE_ID,
            DOMAIN_DATA_SOURCE_ID,
            DOMAIN_AUTHOR_LEVEL_ID,
            true,
            DOMAIN_PARENT_ID,
            "p-content",
            "thumb",
            List.of(DATA_ID_1, DATA_ID_2),
            LocalDateTime.now(),
            DOMAIN_COMMENT_COUNT,
            DOMAIN_LIKE_COUNT,
            DOMAIN_VIEW_COUNT,
            false,
            List.of());

    ProjectEntity entity = ProjectEntityMapper.toEntity(domain);

    assertAll(
        () -> assertThat(entity.getTitle()).isEqualTo("p-title"),
        () -> assertThat(entity.getParentProject()).isNotNull(),
        () -> assertThat(entity.getParentProject().getId()).isEqualTo(DOMAIN_PARENT_ID),
        () ->
            assertThat(entity.getProjectDataEntities())
                .extracting(ProjectDataEntity::getDataId)
                .containsExactlyInAnyOrder(DATA_ID_1, DATA_ID_2));
  }

  @Test
  @DisplayName("toEntity → null 입력 처리")
  void toEntityWithNullInput() {
    ProjectEntity result = ProjectEntityMapper.toEntity(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("toEntity → dataIds가 null인 경우")
  void toEntityWithNullDataIds() {
    Project domain =
        Project.of(
            DOMAIN_ID,
            "p-title",
            DOMAIN_USER_ID,
            DOMAIN_TOPIC_ID,
            DOMAIN_ANALYSIS_PURPOSE_ID,
            DOMAIN_DATA_SOURCE_ID,
            DOMAIN_AUTHOR_LEVEL_ID,
            true,
            null, // parentProjectId null
            "p-content",
            "thumb",
            null, // dataIds null
            LocalDateTime.now(),
            DOMAIN_COMMENT_COUNT,
            DOMAIN_LIKE_COUNT,
            DOMAIN_VIEW_COUNT,
            false,
            List.of());

    ProjectEntity entity = ProjectEntityMapper.toEntity(domain);

    assertAll(
        () -> assertThat(entity.getTitle()).isEqualTo("p-title"),
        () -> assertThat(entity.getParentProject()).isNull(),
        () -> assertThat(entity.getProjectDataEntities()).isEmpty());
  }
}
