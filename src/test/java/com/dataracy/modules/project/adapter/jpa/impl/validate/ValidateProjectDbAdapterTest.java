package com.dataracy.modules.project.adapter.jpa.impl.validate;

import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ValidateProjectDbAdapterTest {

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @InjectMocks
    private ValidateProjectDbAdapter adapter;

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
