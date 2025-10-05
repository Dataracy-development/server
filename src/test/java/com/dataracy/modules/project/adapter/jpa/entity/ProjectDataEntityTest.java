package com.dataracy.modules.project.adapter.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProjectDataEntityTest {

  @Test
  @DisplayName("of 메서드로 ProjectEntity와 dataId를 연결한 엔티티 생성")
  void ofShouldCreateEntityWithProjectAndDataId() {
    // given
    ProjectEntity project = ProjectEntity.builder().id(1L).title("프로젝트").build();

    // when
    ProjectDataEntity entity = ProjectDataEntity.of(project, 100L);

    // then
    assertAll(
        () -> assertThat(entity.getProject()).isEqualTo(project),
        () -> assertThat(entity.getDataId()).isEqualTo(100L));
  }

  @Test
  @DisplayName("assignProject 메서드로 프로젝트를 새로 할당")
  void assignProjectShouldUpdateProjectReference() {
    // given
    ProjectEntity oldProject = ProjectEntity.builder().id(1L).title("프로젝트1").build();
    ProjectEntity newProject = ProjectEntity.builder().id(2L).title("프로젝트2").build();
    ProjectDataEntity entity = ProjectDataEntity.of(oldProject, 200L);

    // when
    entity.assignProject(newProject);

    // then
    assertThat(entity.getProject()).isEqualTo(newProject);
  }
}
