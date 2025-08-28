package com.dataracy.modules.project.adapter.jpa.mapper;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectEntityMapperTest {

    private ProjectEntity sampleEntity() {
        return ProjectEntity.builder()
                .id(1L)
                .title("title")
                .userId(10L)
                .topicId(20L)
                .analysisPurposeId(30L)
                .dataSourceId(40L)
                .authorLevelId(50L)
                .isContinue(true)
                .content("content")
                .thumbnailUrl("thumb.png")
                .commentCount(5L)
                .likeCount(7L)
                .viewCount(100L)
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

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("title");
            assertThat(result.getParentProjectId()).isNull();
            assertThat(result.getDataIds()).isEmpty();
            assertThat(result.getChildProjects()).isEmpty();
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
        ProjectEntity parent = ProjectEntity.builder()
                .id(99L)
                .title("parent")
                .build();

        ProjectEntity child = sampleEntity();
        // child 의 parentProject 지정
        child = ProjectEntity.builder()
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

        assertThat(result.getParentProjectId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("toWithChildren → 지정한 개수만큼 최소 정보 자식 포함")
    void toWithChildren() {
        ProjectEntity child1 = ProjectEntity.builder().id(2L).title("child1").build();
        ProjectEntity child2 = ProjectEntity.builder().id(3L).title("child2").build();

        ProjectEntity parent = sampleEntity();
        parent.getChildProjects().add(child1);
        parent.getChildProjects().add(child2);

        Project result = ProjectEntityMapper.toWithChildren(parent, 1);

        assertThat(result.getChildProjects()).hasSize(1);
        assertThat(result.getChildProjects().get(0).getId()).isIn(2L, 3L);
    }

    @Test
    @DisplayName("toWithData → 데이터 ID 목록을 포함")
    void toWithData() {
        ProjectEntity entity = sampleEntity();
        ProjectDataEntity data1 = ProjectDataEntity.of(entity, 100L);
        ProjectDataEntity data2 = ProjectDataEntity.of(entity, 200L);
        entity.getProjectDataEntities().addAll(Set.of(data1, data2));

        Project result = ProjectEntityMapper.toWithData(entity);

        assertThat(result.getDataIds()).containsExactlyInAnyOrder(100L, 200L);
    }

    @Test
    @DisplayName("toEntity → 도메인 객체를 엔티티로 변환")
    void toEntity() {
        Project domain = Project.of(
                10L,
                "p-title",
                20L,
                30L,
                40L,
                50L,
                60L,
                true,
                99L,
                "p-content",
                "thumb",
                List.of(111L, 222L),
                LocalDateTime.now(),
                5L,
                6L,
                7L,
                false,
                List.of()
        );

        ProjectEntity entity = ProjectEntityMapper.toEntity(domain);

        assertThat(entity.getTitle()).isEqualTo("p-title");
        assertThat(entity.getParentProject()).isNotNull();
        assertThat(entity.getParentProject().getId()).isEqualTo(99L);
        assertThat(entity.getProjectDataEntities())
                .extracting(ProjectDataEntity::getDataId)
                .containsExactlyInAnyOrder(111L, 222L);
    }
}
