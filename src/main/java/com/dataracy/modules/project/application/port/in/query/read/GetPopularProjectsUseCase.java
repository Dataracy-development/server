/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.in.query.read;

import java.util.List;

import com.dataracy.modules.project.application.dto.response.read.PopularProjectResponse;

public interface GetPopularProjectsUseCase {
  /**
   * 지정한 개수만큼 인기 프로젝트 목록을 조회합니다.
   *
   * @param size 반환할 인기 프로젝트의 최대 개수
   * @return 인기 프로젝트 정보를 담은 PopularProjectResponse 객체의 리스트
   */
  List<PopularProjectResponse> getPopularProjects(int size);
}
