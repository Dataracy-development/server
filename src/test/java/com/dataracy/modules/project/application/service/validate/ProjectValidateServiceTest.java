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

    @Mock
    private CheckProjectExistsByIdPort checkProjectExistsByIdPort;

    @InjectMocks
    private ProjectValidateService service;

    @Test
    @DisplayName("프로젝트 검증 성공 - 존재하는 경우")
    void validateProjectSuccess() {
        // given
        Long projectId = 1L;
        given(checkProjectExistsByIdPort.checkProjectExistsById(projectId)).willReturn(true);

        // when & then
        assertThatNoException().isThrownBy(() -> service.validateProject(projectId));
        then(checkProjectExistsByIdPort).should().checkProjectExistsById(projectId);
    }

    @Test
    @DisplayName("프로젝트 검증 실패 - 존재하지 않는 경우 예외 발생")
    void validateProjectFailWhenNotFound() {
        // given
        Long projectId = 2L;
        given(checkProjectExistsByIdPort.checkProjectExistsById(projectId)).willReturn(false);

        // when
        ProjectException ex = catchThrowableOfType(
                () -> service.validateProject(projectId),
                ProjectException.class
        );

        // then
        then(checkProjectExistsByIdPort).should().checkProjectExistsById(projectId);
        org.assertj.core.api.Assertions.assertThat(ex).isNotNull();
    }
}
