/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.out.query.read;

import java.util.List;

import com.dataracy.modules.project.domain.model.Project;

public interface GetPopularProjectsPort {
  /**
   * 지정한 개수만큼 인기 프로젝트 목록을 반환합니다.
   *
   * @param size 조회할 인기 프로젝트의 최대 개수
   * @return 인기 프로젝트의 리스트
   */
  List<Project> getPopularProjects(int size);
}
