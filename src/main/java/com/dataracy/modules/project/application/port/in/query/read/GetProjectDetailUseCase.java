/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.in.query.read;

import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;

public interface GetProjectDetailUseCase {
  /**
   * 프로젝트 ID, 사용자 ID, 뷰어 ID를 기반으로 해당 프로젝트의 상세 정보를 조회합니다.
   *
   * @param projectId 조회할 프로젝트의 고유 식별자
   * @param userId 요청 사용자의 고유 식별자
   * @param viewerId 프로젝트 상세 정보를 조회하는 뷰어의 고유 식별자
   * @return 프로젝트의 상세 정보가 담긴 ProjectDetailResponse 객체
   */
  ProjectDetailResponse getProjectDetail(Long projectId, Long userId, String viewerId);
}
