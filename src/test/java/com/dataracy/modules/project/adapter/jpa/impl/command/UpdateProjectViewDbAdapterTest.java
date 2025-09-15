package com.dataracy.modules.project.adapter.jpa.impl.command;

import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UpdateProjectViewDbAdapterTest {

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @InjectMocks
    private UpdateProjectViewDbAdapter adapter;

    @Test
    @DisplayName("성공 → projectJpaRepository.increaseViewCount 가 호출된다")
    void increaseViewCountSuccess() {
        // given
        Long projectId = 1L;
        Long count = 5L;

        // when
        adapter.increaseViewCount(projectId, count);

        // then
        then(projectJpaRepository).should().increaseViewCount(projectId, count);
    }
}
