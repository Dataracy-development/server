package com.dataracy.modules.project.application.service.validate;

import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByIdPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectValidateServiceTest {

    @Mock
    private CheckProjectExistsByIdPort checkProjectExistsByIdPort;

    @InjectMocks
    private ProjectValidateService service;

    private MockedStatic<com.dataracy.modules.common.logging.support.LoggerFactory> loggerFactoryMock;
    private com.dataracy.modules.common.logging.ServiceLogger loggerService;

    @BeforeEach
    void setUp() {
        loggerFactoryMock = mockStatic(com.dataracy.modules.common.logging.support.LoggerFactory.class);
        loggerService = mock(com.dataracy.modules.common.logging.ServiceLogger.class);
        loggerFactoryMock.when(() -> com.dataracy.modules.common.logging.support.LoggerFactory.service()).thenReturn(loggerService);
        lenient().when(loggerService.logStart(anyString(), anyString())).thenReturn(Instant.now());
        lenient().doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
        lenient().doNothing().when(loggerService).logWarning(anyString(), anyString());
    }

    @AfterEach
    void tearDown() {
        if (loggerFactoryMock != null) {
            loggerFactoryMock.close();
        }
    }

    @Nested
    @DisplayName("프로젝트 존재 검증")
    class ValidateProject {

        @Test
        @DisplayName("프로젝트 검증 성공 - 존재하는 경우 및 로깅 검증")
        void validateProjectSuccess() {
            // given
            Long projectId = 1L;
            given(checkProjectExistsByIdPort.checkProjectExistsById(projectId)).willReturn(true);

            // when & then
            assertThatNoException().isThrownBy(() -> service.validateProject(projectId));
            then(checkProjectExistsByIdPort).should().checkProjectExistsById(projectId);
            
            // 로깅 검증
            then(loggerService).should().logStart(eq("ValidateProjectUseCase"), 
                contains("프로젝트 존재 유효성 검사 서비스 시작"));
            then(loggerService).should().logSuccess(eq("ValidateProjectUseCase"), 
                contains("프로젝트 존재 유효성 검사 서비스 종료"), any(Instant.class));
            then(loggerService).should(never()).logWarning(anyString(), anyString());
        }

        @Test
        @DisplayName("프로젝트 검증 실패 - 존재하지 않는 경우 및 로깅 검증")
        void validateProjectFailWhenNotExists() {
            // given
            Long projectId = 999L;
            given(checkProjectExistsByIdPort.checkProjectExistsById(projectId)).willReturn(false);

            // when & then
            ProjectException ex = catchThrowableOfType(
                    () -> service.validateProject(projectId),
                    ProjectException.class
            );
            
            // 예외 검증
            assertThat(ex.getErrorCode()).isEqualTo(ProjectErrorStatus.NOT_FOUND_PROJECT);
            
            // 포트 호출 검증
            then(checkProjectExistsByIdPort).should().checkProjectExistsById(projectId);
            
            // 로깅 검증
            then(loggerService).should().logStart(eq("ValidateProjectUseCase"), 
                contains("프로젝트 존재 유효성 검사 서비스 시작"));
            then(loggerService).should().logWarning(eq("ValidateProjectUseCase"), 
                contains("해당 프로젝트가 존재하지 않습니다"));
            then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
        }

        @Test
        @DisplayName("null 프로젝트 ID → IllegalArgumentException 발생")
        void validateProjectFailWhenProjectIdIsNull() {
            // given
            Long projectId = null;
            given(checkProjectExistsByIdPort.checkProjectExistsById(projectId))
                    .willThrow(new IllegalArgumentException("Project ID cannot be null"));

            // when & then
            IllegalArgumentException ex = catchThrowableOfType(
                    () -> service.validateProject(projectId),
                    IllegalArgumentException.class
            );
            
            assertThat(ex.getMessage()).contains("Project ID cannot be null");
            
            // 로깅 검증
            then(loggerService).should().logStart(eq("ValidateProjectUseCase"), 
                contains("프로젝트 존재 유효성 검사 서비스 시작"));
        }

        @Test
        @DisplayName("포트에서 예외 발생 시 전파 확인")
        void validateProjectFailWhenPortThrowsException() {
            // given
            Long projectId = 1L;
            given(checkProjectExistsByIdPort.checkProjectExistsById(projectId))
                    .willThrow(new RuntimeException("Database connection failed"));

            // when & then
            RuntimeException ex = catchThrowableOfType(
                    () -> service.validateProject(projectId),
                    RuntimeException.class
            );
            
            assertThat(ex.getMessage()).contains("Database connection failed");
            
            // 로깅 검증
            then(loggerService).should().logStart(eq("ValidateProjectUseCase"), 
                contains("프로젝트 존재 유효성 검사 서비스 시작"));
        }
    }
}