/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.jpa.impl.command;

import org.springframework.stereotype.Repository;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectLikePort;

import lombok.RequiredArgsConstructor;

@Repository("updateProjectLikeDbAdapter")
@RequiredArgsConstructor
public class UpdateProjectLikeDbAdapter implements UpdateProjectLikePort {
  private final ProjectJpaRepository projectJpaRepository;

  // Entity 상수 정의
  private static final String PROJECT_ENTITY = "ProjectEntity";

  /**
   * 지정된 프로젝트의 좋아요 수를 1 증가시킵니다.
   *
   * @param projectId 좋아요 수를 증가시킬 프로젝트의 ID
   */
  @Override
  public void increaseLikeCount(Long projectId) {
    projectJpaRepository.increaseLikeCount(projectId);
    LoggerFactory.db()
        .logUpdate(PROJECT_ENTITY, String.valueOf(projectId), "프로젝트 DB 좋아요 1증가가 완료되었습니다.");
  }

  /**
   * 지정된 프로젝트의 좋아요 수를 1 감소시킵니다.
   *
   * @param projectId 좋아요 수를 감소시킬 프로젝트의 ID
   */
  @Override
  public void decreaseLikeCount(Long projectId) {
    projectJpaRepository.decreaseLikeCount(projectId);
    LoggerFactory.db()
        .logUpdate(PROJECT_ENTITY, String.valueOf(projectId), "프로젝트 DB 좋아요 1감소가 완료되었습니다.");
  }
}
