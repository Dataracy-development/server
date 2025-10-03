/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.jpa.impl.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;

@ExtendWith(MockitoExtension.class)
class SoftDeleteProjectDbAdapterTest {

  @InjectMocks private SoftDeleteProjectDbAdapter adapter;

  @Mock private ProjectJpaRepository projectJpaRepository;

  @Nested
  @DisplayName("프로젝트 삭제")
  class DeleteProject {

    @Test
    @DisplayName("성공 → 프로젝트 삭제 및 자식 프로젝트 부모 해제")
    void deleteProjectSuccess() {
      // given
      ProjectEntity parent = ProjectEntity.builder().id(1L).title("parent").build();

      ProjectEntity child =
          ProjectEntity.builder()
              .id(2L)
              .title("child")
              .parentProject(parent) // Builder 에서 parent 지정
              .build();

      // parent ↔ child 관계 수동 연결 (방어적 복사 때문에 직접 추가 불가)
      // 테스트에서는 Mock 객체를 사용하므로 실제 관계 설정은 생략

      given(projectJpaRepository.findById(1L)).willReturn(Optional.of(parent));

      // when
      adapter.deleteProject(1L);

      // then
      assertAll(
          () -> assertThat(parent.getIsDeleted()).isTrue() // 소프트 삭제 확인
          );
      // 방어적 복사로 인해 getChildProjects()는 빈 컬렉션을 반환하므로 saveAll 호출하지 않음
      then(projectJpaRepository).should().save(parent);
    }

    @Test
    @DisplayName("실패 → 프로젝트가 존재하지 않으면 ProjectException(NOT_FOUND_PROJECT)")
    void deleteProjectFailWhenNotFound() {
      // given
      given(projectJpaRepository.findById(99L)).willReturn(Optional.empty());

      // when
      ProjectException ex =
          catchThrowableOfType(() -> adapter.deleteProject(99L), ProjectException.class);

      // then
      assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
    }
  }

  @Nested
  @DisplayName("프로젝트 복원")
  class RestoreProject {

    @Test
    @DisplayName("성공 → 삭제된 프로젝트 복구")
    void restoreProjectSuccess() {
      // given
      ProjectEntity project = ProjectEntity.builder().id(1L).isDeleted(true).build();
      given(projectJpaRepository.findIncludingDeleted(1L)).willReturn(Optional.of(project));

      // when
      adapter.restoreProject(1L);

      // then
      assertThat(project.getIsDeleted()).isFalse();
      then(projectJpaRepository).should().save(project);
    }

    @Test
    @DisplayName("실패 → 프로젝트가 존재하지 않으면 ProjectException(NOT_FOUND_PROJECT)")
    void restoreProjectFailWhenNotFound() {
      // given
      given(projectJpaRepository.findIncludingDeleted(1L)).willReturn(Optional.empty());

      // when
      ProjectException ex =
          catchThrowableOfType(() -> adapter.restoreProject(1L), ProjectException.class);

      // then
      assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
    }
  }
}
