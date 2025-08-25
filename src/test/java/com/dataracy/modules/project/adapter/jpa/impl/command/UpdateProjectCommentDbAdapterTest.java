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
class UpdateProjectCommentDbAdapterTest {

    @Mock ProjectJpaRepository projectJpaRepository;
    @InjectMocks UpdateProjectCommentDbAdapter adapter;

    @Test
    @DisplayName("increaseCommentCount_should_delegate_to_repository")
    void increaseCommentCount_should_delegate_to_repository() {
        // given
        Long projectId = 1L;

        // when
        adapter.increaseCommentCount(projectId);

        // then
        then(projectJpaRepository).should().increaseCommentCount(projectId);
    }

    @Test
    @DisplayName("decreaseCommentCount_should_delegate_to_repository")
    void decreaseCommentCount_should_delegate_to_repository() {
        // given
        Long projectId = 2L;

        // when
        adapter.decreaseCommentCount(projectId);

        // then
        then(projectJpaRepository).should().decreaseCommentCount(projectId);
    }
}
