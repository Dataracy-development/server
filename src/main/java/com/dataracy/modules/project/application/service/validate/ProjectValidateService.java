package com.dataracy.modules.project.application.service.validate;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.port.in.validate.ValidateProjectUseCase;
import com.dataracy.modules.project.application.port.out.query.validate.CheckProjectExistsByIdPort;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectValidateService implements ValidateProjectUseCase {
  private final CheckProjectExistsByIdPort checkProjectExistsByIdPort;

  // Use Case 상수 정의
  private static final String VALIDATE_PROJECT_USE_CASE = "ValidateProjectUseCase";

  // 메시지 상수 정의
  private static final String PROJECT_NOT_FOUND_MESSAGE = "해당 프로젝트가 존재하지 않습니다. projectId=";

  /**
   * 주어진 프로젝트 ID에 해당하는 프로젝트의 존재 여부를 검증합니다.
   *
   * <p>프로젝트가 존재하지 않을 경우 {@code ProjectException}을 발생시킵니다.
   *
   * @param projectId 존재 여부를 확인할 프로젝트의 ID
   * @throws ProjectException 프로젝트가 존재하지 않을 때 발생
   */
  @Override
  @Transactional(readOnly = true)
  public void validateProject(Long projectId) {
    Instant startTime =
        LoggerFactory.service()
            .logStart(VALIDATE_PROJECT_USE_CASE, "프로젝트 존재 유효성 검사 서비스 시작 projectId=" + projectId);

    boolean isValidate = checkProjectExistsByIdPort.checkProjectExistsById(projectId);
    if (!isValidate) {
      LoggerFactory.service()
          .logWarning(VALIDATE_PROJECT_USE_CASE, PROJECT_NOT_FOUND_MESSAGE + projectId);
      throw new ProjectException(ProjectErrorStatus.NOT_FOUND_PROJECT);
    }
    LoggerFactory.service()
        .logSuccess(
            VALIDATE_PROJECT_USE_CASE, "프로젝트 존재 유효성 검사 서비스 종료 projectId=" + projectId, startTime);
  }
}
