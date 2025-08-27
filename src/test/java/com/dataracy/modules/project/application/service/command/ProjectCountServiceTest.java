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

    @Mock
    private UpdateProjectCommentPort updateProjectCommentDbPort;

    @Mock
    private UpdateProjectLikePort updateProjectLikeDbPort;

    @Mock
    private ManageProjectProjectionTaskPort manageProjectProjectionTaskPort;

    @InjectMocks
    private ProjectCountService service;

    @Test
    @DisplayName("댓글 수 증가 성공")
    void increaseCommentCountSuccess() {
        // given
        Long projectId = 10L;

        // when & then
        assertThatNoException().isThrownBy(() -> service.increaseCommentCount(projectId));

        then(updateProjectCommentDbPort).should().increaseCommentCount(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueCommentDelta(projectId, 1);
        then(updateProjectLikeDbPort).should(never()).increaseLikeCount(projectId);
    }

    @Test
    @DisplayName("댓글 수 감소 성공")
    void decreaseCommentCountSuccess() {
        // given
        Long projectId = 11L;

        // when & then
        assertThatNoException().isThrownBy(() -> service.decreaseCommentCount(projectId));

        then(updateProjectCommentDbPort).should().decreaseCommentCount(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueCommentDelta(projectId, -1);
    }

    @Test
    @DisplayName("좋아요 수 증가 성공")
    void increaseLikeCountSuccess() {
        // given
        Long projectId = 12L;

        // when & then
        assertThatNoException().isThrownBy(() -> service.increaseLikeCount(projectId));

        then(updateProjectLikeDbPort).should().increaseLikeCount(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueLikeDelta(projectId, 1);
    }

    @Test
    @DisplayName("좋아요 수 감소 성공")
    void decreaseLikeCountSuccess() {
        // given
        Long projectId = 13L;

        // when & then
        assertThatNoException().isThrownBy(() -> service.decreaseLikeCount(projectId));

        then(updateProjectLikeDbPort).should().decreaseLikeCount(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueLikeDelta(projectId, -1);
    }
}
