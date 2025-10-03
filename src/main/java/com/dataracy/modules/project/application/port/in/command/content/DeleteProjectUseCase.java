/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.in.command.content;

public interface DeleteProjectUseCase {
  /**
   * 지정된 프로젝트를 삭제 상태로 변경합니다.
   *
   * @param projectId 삭제 처리할 프로젝트의 고유 식별자
   */
  void deleteProject(Long projectId);
}
