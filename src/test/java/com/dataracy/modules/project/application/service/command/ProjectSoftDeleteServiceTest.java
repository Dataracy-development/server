/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.service.command;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.common.logging.ServiceLogger;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.port.out.command.delete.SoftDeleteProjectPort;
import com.dataracy.modules.project.application.port.out.command.projection.ManageProjectProjectionTaskPort;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProjectSoftDeleteServiceTest {

  @Mock private SoftDeleteProjectPort softDeleteProjectDbPort;

  @Mock private ManageProjectProjectionTaskPort manageProjectProjectionTaskPort;

  private ProjectSoftDeleteService service;
  private MockedStatic<LoggerFactory> loggerFactoryMock;
  private ServiceLogger loggerService;

  @BeforeEach
  void setUp() {
    service =
        new ProjectSoftDeleteService(softDeleteProjectDbPort, manageProjectProjectionTaskPort);
    loggerFactoryMock = mockStatic(LoggerFactory.class);
    loggerService = mock(ServiceLogger.class);
    loggerFactoryMock.when(LoggerFactory::service).thenReturn(loggerService);
    doReturn(Instant.now()).when(loggerService).logStart(anyString(), anyString());
    doNothing().when(loggerService).logSuccess(anyString(), anyString(), any(Instant.class));
  }

  @AfterEach
  void tearDown() {
    if (loggerFactoryMock != null) {
      loggerFactoryMock.close();
    }
  }

  @Nested
  @DisplayName("deleteProject 메서드 테스트")
  class DeleteProjectTest {

    @Test
    @DisplayName("프로젝트 소프트 삭제 성공")
    void deleteProjectSuccess() {
      // given
      Long projectId = 1L;
      willDoNothing().given(softDeleteProjectDbPort).deleteProject(projectId);
      willDoNothing().given(manageProjectProjectionTaskPort).enqueueSetDeleted(projectId, true);

      // when
      service.deleteProject(projectId);

      // then
      then(softDeleteProjectDbPort).should().deleteProject(projectId);
      then(manageProjectProjectionTaskPort).should().enqueueSetDeleted(projectId, true);
      then(loggerService)
          .should()
          .logStart(
              eq("DeleteProjectUseCase"),
              contains("프로젝트 소프트 delete 삭제 서비스 시작 projectId=" + projectId));
      then(loggerService)
          .should()
          .logSuccess(
              eq("DeleteProjectUseCase"),
              contains("프로젝트 소프트 delete 삭제 서비스 종료 projectId=" + projectId),
              any(Instant.class));
    }

    @Test
    @DisplayName("프로젝트 소프트 삭제 실패 - DB 포트 예외")
    void deleteProjectFailWhenDbPortThrowsException() {
      // given
      Long projectId = 1L;
      RuntimeException exception = new RuntimeException("Database error");
      willThrow(exception).given(softDeleteProjectDbPort).deleteProject(projectId);

      // when & then
      try {
        service.deleteProject(projectId);
      } catch (RuntimeException e) {
        // 예외가 전파되어야 함
      }

      then(softDeleteProjectDbPort).should().deleteProject(projectId);
      then(manageProjectProjectionTaskPort).should(never()).enqueueSetDeleted(any(), anyBoolean());
      then(loggerService)
          .should()
          .logStart(
              eq("DeleteProjectUseCase"),
              contains("프로젝트 소프트 delete 삭제 서비스 시작 projectId=" + projectId));
      then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
    }
  }

  @Nested
  @DisplayName("restoreProject 메서드 테스트")
  class RestoreProjectTest {

    @Test
    @DisplayName("프로젝트 복원 성공")
    void restoreProjectSuccess() {
      // given
      Long projectId = 1L;
      willDoNothing().given(softDeleteProjectDbPort).restoreProject(projectId);
      willDoNothing().given(manageProjectProjectionTaskPort).enqueueSetDeleted(projectId, false);

      // when
      service.restoreProject(projectId);

      // then
      then(softDeleteProjectDbPort).should().restoreProject(projectId);
      then(manageProjectProjectionTaskPort).should().enqueueSetDeleted(projectId, false);
      then(loggerService)
          .should()
          .logStart(
              eq("RestoreProjectUseCase"),
              contains("프로젝트 소프트 delete 복원 서비스 시작 projectId=" + projectId));
      then(loggerService)
          .should()
          .logSuccess(
              eq("RestoreProjectUseCase"),
              contains("프로젝트 소프트 delete 복원 서비스 종료 projectId=" + projectId),
              any(Instant.class));
    }

    @Test
    @DisplayName("프로젝트 복원 실패 - DB 포트 예외")
    void restoreProjectFailWhenDbPortThrowsException() {
      // given
      Long projectId = 1L;
      RuntimeException exception = new RuntimeException("Database error");
      willThrow(exception).given(softDeleteProjectDbPort).restoreProject(projectId);

      // when & then
      try {
        service.restoreProject(projectId);
      } catch (RuntimeException e) {
        // 예외가 전파되어야 함
      }

      then(softDeleteProjectDbPort).should().restoreProject(projectId);
      then(manageProjectProjectionTaskPort).should(never()).enqueueSetDeleted(any(), anyBoolean());
      then(loggerService)
          .should()
          .logStart(
              eq("RestoreProjectUseCase"),
              contains("프로젝트 소프트 delete 복원 서비스 시작 projectId=" + projectId));
      then(loggerService).should(never()).logSuccess(anyString(), anyString(), any(Instant.class));
    }
  }
}
