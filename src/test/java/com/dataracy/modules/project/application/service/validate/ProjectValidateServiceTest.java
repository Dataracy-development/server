package com.dataracy.modules.project.application.service.validate;

import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByIdPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectValidateServiceTest {

    @Mock
    private CheckProjectExistsByIdPort checkProjectExistsByIdPort;

    private ProjectValidateService projectValidateService;

    @BeforeEach
    void setUp() {
        projectValidateService = new ProjectValidateService(checkProjectExistsByIdPort);
    }

    @Nested
    @DisplayName("validateProject 메서드 테스트")
    class ValidateProjectTest {

        @Test
        @DisplayName("성공: 프로젝트가 존재할 때 검증 통과")
        void validateProject_프로젝트존재_검증통과() {
            // given
            Long projectId = 1L;
            given(checkProjectExistsByIdPort.checkProjectExistsById(projectId)).willReturn(true);

            // when & then
            projectValidateService.validateProject(projectId);
            
            then(checkProjectExistsByIdPort).should().checkProjectExistsById(projectId);
        }

        @Test
        @DisplayName("실패: 프로젝트가 존재하지 않을 때 ProjectException 발생")
        void validateProject_프로젝트존재하지않음_ProjectException발생() {
            // given
            Long projectId = 999L;
            given(checkProjectExistsByIdPort.checkProjectExistsById(projectId)).willReturn(false);

            // when & then
            ProjectException exception = catchThrowableOfType(
                    () -> projectValidateService.validateProject(projectId),
                    ProjectException.class
            );
            assertAll(
                    () -> org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ProjectException.class)
            );
            
            then(checkProjectExistsByIdPort).should().checkProjectExistsById(projectId);
        }
    }
}
