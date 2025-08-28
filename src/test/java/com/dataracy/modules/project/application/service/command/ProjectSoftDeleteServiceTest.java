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

    @Mock
    private SoftDeleteProjectPort softDeleteProjectDbPort;

    @Mock
    private ManageProjectProjectionTaskPort manageProjectProjectionTaskPort;

    @InjectMocks
    private ProjectSoftDeleteService service;

    @Test
    @DisplayName("프로젝트 삭제 성공 - 소프트 삭제 및 인덱싱 반영")
    void deleteProjectSuccess() {
        // given
        Long projectId = 100L;

        // when & then
        assertThatNoException().isThrownBy(() -> service.deleteProject(projectId));

        then(softDeleteProjectDbPort).should().deleteProject(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueSetDeleted(projectId, true);
    }

    @Test
    @DisplayName("프로젝트 복원 성공 - 복원 및 인덱싱 반영")
    void restoreProjectSuccess() {
        // given
        Long projectId = 101L;

        // when & then
        assertThatNoException().isThrownBy(() -> service.restoreProject(projectId));

        then(softDeleteProjectDbPort).should().restoreProject(projectId);
        then(manageProjectProjectionTaskPort).should().enqueueSetDeleted(projectId, false);
    }
}
