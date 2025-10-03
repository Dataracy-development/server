/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.out.command.create;

import com.dataracy.modules.project.domain.model.Project;

public interface CreateProjectPort {
  /**
   * 프로젝트 엔티티를 저장소에 저장하고, 저장된 프로젝트 객체를 반환합니다.
   *
   * @param project 저장할 프로젝트 객체
   * @return 저장된 프로젝트 엔티티
   */
  Project saveProject(Project project);
}
