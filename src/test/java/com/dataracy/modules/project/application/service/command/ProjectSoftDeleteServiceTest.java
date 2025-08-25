package com.dataracy.modules.project.application.service.command;

import com.dataracy.modules.project.application.port.out.command.delete.SoftDeleteProjectPort;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionTaskPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProjectSoftDeleteServiceTest {

    @Mock SoftDeleteProjectPort softDeleteProjectDbPort;
    @Mock ManageProjectProjectionTaskPort manageProjectProjectionTaskPort;

    @InjectMocks ProjectSoftDeleteService service;

    @Test
    @DisplayName("deleteProject_should_soft_delete_and_enqueue_setDeleted_true")
    void deleteProject_should_soft_delete_and_enqueue_setDeleted_true() {
        // given
        Long projectId = 100L;

        // when
        assertThatNoException().isThrownBy(() -> service.deleteProject(projectId));

        // then
        then(softDeleteProjectDbPort).should().deleteProject(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueSetDeleted(projectId, true);
    }

    @Test
    @DisplayName("restoreProject_should_restore_and_enqueue_setDeleted_false")
    void restoreProject_should_restore_and_enqueue_setDeleted_false() {
        // given
        Long projectId = 101L;

        // when
        assertThatNoException().isThrownBy(() -> service.restoreProject(projectId));

        // then
        then(softDeleteProjectDbPort).should().restoreProject(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueSetDeleted(projectId, false);
    }
}
