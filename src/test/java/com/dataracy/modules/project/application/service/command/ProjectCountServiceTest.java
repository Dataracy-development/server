package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionTaskPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectCommentPort;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectLikePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProjectCountServiceTest {

    @Mock UpdateProjectCommentPort updateProjectCommentDbPort;
    @Mock UpdateProjectLikePort updateProjectLikeDbPort;
    @Mock ManageProjectProjectionTaskPort manageProjectProjectionTaskPort;

    @InjectMocks ProjectCountService service;

    @Test
    @DisplayName("increaseCommentCount_should_update_db_and_enqueue_projection_delta")
    void increaseCommentCount_should_update_db_and_enqueue_projection_delta() {
        // given
        Long projectId = 10L;

        // when
        assertThatNoException().isThrownBy(() -> service.increaseCommentCount(projectId));

        // then
        then(updateProjectCommentDbPort).should().increaseCommentCount(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueCommentDelta(projectId, 1);
        then(updateProjectLikeDbPort).should(never()).increaseLikeCount(projectId);
    }

    @Test
    @DisplayName("decreaseCommentCount_should_update_db_and_enqueue_projection_delta_minus1")
    void decreaseCommentCount_should_update_db_and_enqueue_projection_delta_minus1() {
        // given
        Long projectId = 11L;

        // when
        assertThatNoException().isThrownBy(() -> service.decreaseCommentCount(projectId));

        // then
        then(updateProjectCommentDbPort).should().decreaseCommentCount(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueCommentDelta(projectId, -1);
    }

    @Test
    @DisplayName("increaseLikeCount_should_update_db_and_enqueue_projection_delta")
    void increaseLikeCount_should_update_db_and_enqueue_projection_delta() {
        // given
        Long projectId = 12L;

        // when
        assertThatNoException().isThrownBy(() -> service.increaseLikeCount(projectId));

        // then
        then(updateProjectLikeDbPort).should().increaseLikeCount(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueLikeDelta(projectId, 1);
    }

    @Test
    @DisplayName("decreaseLikeCount_should_update_db_and_enqueue_projection_delta_minus1")
    void decreaseLikeCount_should_update_db_and_enqueue_projection_delta_minus1() {
        // given
        Long projectId = 13L;

        // when
        assertThatNoException().isThrownBy(() -> service.decreaseLikeCount(projectId));

        // then
        then(updateProjectLikeDbPort).should().decreaseLikeCount(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueLikeDelta(projectId, -1);
    }
}
