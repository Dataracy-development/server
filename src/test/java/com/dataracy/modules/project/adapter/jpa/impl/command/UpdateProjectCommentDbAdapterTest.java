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
class UpdateProjectCommentDbAdapterTest {

  @Mock private ProjectJpaRepository projectJpaRepository;

  @InjectMocks private UpdateProjectCommentDbAdapter adapter;

  @Nested
  @DisplayName("댓글 수 증가")
  class IncreaseCommentCount {

    @Test
    @DisplayName("성공 → projectJpaRepository.increaseCommentCount 가 호출된다")
    void increaseCommentCountSuccess() {
      // given
      Long projectId = 1L;

      // when
      adapter.increaseCommentCount(projectId);

      // then
      then(projectJpaRepository).should().increaseCommentCount(projectId);
    }
  }

  @Nested
  @DisplayName("댓글수 감소")
  class DecreaseCommentCount {

    @Test
    @DisplayName("성공 → projectJpaRepository.decreaseCommentCount 가 호출된다")
    void decreaseCommentCountSuccess() {
      // given
      Long projectId = 2L;

      // when
      adapter.decreaseCommentCount(projectId);

      // then
      then(projectJpaRepository).should().decreaseCommentCount(projectId);
    }
  }
}
