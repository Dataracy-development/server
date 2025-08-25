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
class UpdateProjectLikeDbAdapterTest {

    @Mock ProjectJpaRepository projectJpaRepository;
    @InjectMocks UpdateProjectLikeDbAdapter adapter;

    @Test
    @DisplayName("increaseLikeCount_should_delegate_to_repository")
    void increaseLikeCount_should_delegate_to_repository() {
        // given
        Long projectId = 1L;

        // when
        adapter.increaseLikeCount(projectId);

        // then
        then(projectJpaRepository).should().increaseLikeCount(projectId);
    }

    @Test
    @DisplayName("decreaseLikeCount_should_delegate_to_repository")
    void decreaseLikeCount_should_delegate_to_repository() {
        // given
        Long projectId = 2L;

        // when
        adapter.decreaseLikeCount(projectId);

        // then
        then(projectJpaRepository).should().decreaseLikeCount(projectId);
    }
}
