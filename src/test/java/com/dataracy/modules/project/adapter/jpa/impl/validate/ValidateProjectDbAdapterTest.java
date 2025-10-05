package com.dataracy.modules.project.adapter.jpa.impl.validate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ValidateProjectDbAdapterTest {

  @Mock private ProjectJpaRepository projectJpaRepository;

  @InjectMocks private ValidateProjectDbAdapter adapter;

  @Test
  @DisplayName("checkProjectExistsById → 존재하면 true 반환")
  void checkProjectExistsByIdTrue() {
    // given
    Long projectId = 1L;
    given(projectJpaRepository.existsById(projectId)).willReturn(true);

    // when
    boolean result = adapter.checkProjectExistsById(projectId);

    // then
    assertThat(result).isTrue();
    then(projectJpaRepository).should().existsById(projectId);
  }

  @Test
  @DisplayName("checkProjectExistsById → 존재하지 않으면 false 반환")
  void checkProjectExistsByIdFalse() {
    // given
    Long projectId = 2L;
    given(projectJpaRepository.existsById(projectId)).willReturn(false);

    // when
    boolean result = adapter.checkProjectExistsById(projectId);

    // then
    assertThat(result).isFalse();
    then(projectJpaRepository).should().existsById(projectId);
  }
}
