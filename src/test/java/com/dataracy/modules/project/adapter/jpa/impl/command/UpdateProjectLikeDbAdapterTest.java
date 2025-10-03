/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.jpa.impl.command;

import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;

@ExtendWith(MockitoExtension.class)
class UpdateProjectLikeDbAdapterTest {

  @Mock private ProjectJpaRepository projectJpaRepository;

  @InjectMocks private UpdateProjectLikeDbAdapter adapter;

  @Nested
  @DisplayName("좋아요 수 증가")
  class IncreaseLikeCount {

    @Test
    @DisplayName("성공 → projectJpaRepository.increaseLikeCount 가 호출된다")
    void increaseLikeCountSuccess() {
      // given
      Long projectId = 1L;

      // when
      adapter.increaseLikeCount(projectId);

      // then
      then(projectJpaRepository).should().increaseLikeCount(projectId);
    }
  }

  @Nested
  @DisplayName("좋아요 수 감소")
  class DecreaseLikeCount {

    @Test
    @DisplayName("성공 → projectJpaRepository.decreaseLikeCount 가 호출된다")
    void decreaseLikeCountSuccess() {
      // given
      Long projectId = 2L;

      // when
      adapter.decreaseLikeCount(projectId);

      // then
      then(projectJpaRepository).should().decreaseLikeCount(projectId);
    }
  }
}
