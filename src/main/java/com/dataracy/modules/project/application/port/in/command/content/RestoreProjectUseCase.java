/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.in.command.content;

public interface RestoreProjectUseCase {
  /**
   * 지정된 프로젝트를 복구 상태로 전환합니다.
   *
   * @param projectId 복구할 프로젝트의 고유 식별자
   */
  void restoreProject(Long projectId);
}
