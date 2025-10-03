/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.application.port.out.query.read;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dataracy.modules.project.domain.model.Project;

public interface FindConnectedProjectsPort {
  /**
   * 주어진 데이터 ID에 연결된 프로젝트들을 페이지 단위로 조회합니다.
   *
   * @param dataId 연결된 프로젝트를 조회할 데이터셋의 ID
   * @param pageable 결과 페이지네이션을 위한 정보
   * @return 데이터셋과 연관된 프로젝트의 페이지 결과
   */
  Page<Project> findConnectedProjectsAssociatedWithDataset(Long dataId, Pageable pageable);
}
