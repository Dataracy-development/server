package com.dataracy.modules.project.application.service.validate;

import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByIdPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProjectValidateServiceTest {

    @Mock CheckProjectExistsByIdPort checkProjectExistsByIdPort;

    @InjectMocks ProjectValidateService service;

    @Test
    @DisplayName("validateProject_should_pass_when_project_exists")
    void validateProject_should_pass_when_project_exists() {
        // given
        Long projectId = 1L;
        given(checkProjectExistsByIdPort.checkProjectExistsById(projectId)).willReturn(true);

        // when & then
        assertThatNoException().isThrownBy(() -> service.validateProject(projectId));
        then(checkProjectExistsByIdPort).should().checkProjectExistsById(projectId);
    }

    @Test
    @DisplayName("validateProject_should_throw_when_not_found")
    void validateProject_should_throw_when_not_found() {
        // given
        Long projectId = 2L;
        given(checkProjectExistsByIdPort.checkProjectExistsById(projectId)).willReturn(false);

        // when
        ProjectException ex = catchThrowableOfType(() -> service.validateProject(projectId), ProjectException.class);

        // then
        then(checkProjectExistsByIdPort).should().checkProjectExistsById(projectId);
        org.assertj.core.api.Assertions.assertThat(ex).isNotNull();
    }
}
