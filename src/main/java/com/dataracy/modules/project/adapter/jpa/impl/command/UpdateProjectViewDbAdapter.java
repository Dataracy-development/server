/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.jpa.impl.command;

import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.jpa.repository.ProjectJpaRepository;
import com.dataracy.modules.project.application.port.out.command.update.UpdateProjectViewPort;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Repository("updateProjectViewDbAdapter")
@RequiredArgsConstructor
public class UpdateProjectViewDbAdapter implements UpdateProjectViewPort {
  private final ProjectJpaRepository projectJpaRepository;

  @PersistenceContext private EntityManager entityManager;

  // Entity 상수 정의
  private static final String PROJECT_ENTITY = "ProjectEntity";

  /**
   * 지정된 프로젝트의 조회수를 특정 수치만큼 증가시킵니다.
   *
   * @param projectId 조회수를 증가시킬 프로젝트의 ID
   * @param count 증가시킬 조회수
   */
  @Override
  public void increaseViewCount(Long projectId, Long count) {
    projectJpaRepository.increaseViewCount(projectId, count);
    LoggerFactory.db()
        .logUpdate(
            PROJECT_ENTITY, String.valueOf(projectId), "프로젝트 DB 조회수 " + count + "증가가 완료되었습니다.");
  }

  /**
   * 여러 프로젝트의 조회수를 배치로 증가시킵니다.
   *
   * @param viewCountUpdates 프로젝트 ID와 증가시킬 조회수 값의 맵
   */
  @Override
  @Transactional
  public void increaseViewCountBatch(Map<Long, Long> viewCountUpdates) {
    if (viewCountUpdates.isEmpty()) {
      return;
    }

    // ✅ 진짜 배치 처리: 하나의 쿼리로 모든 프로젝트 업데이트
    StringBuilder caseStatements = new StringBuilder();
    for (Map.Entry<Long, Long> entry : viewCountUpdates.entrySet()) {
      caseStatements
          .append("WHEN ")
          .append(entry.getKey())
          .append(" THEN ")
          .append(entry.getValue())
          .append(" ");
    }

    String query =
        "UPDATE project SET view_count = view_count + "
            + "CASE project_id "
            + caseStatements.toString()
            + "END "
            + "WHERE project_id IN ("
            + String.join(
                ",", viewCountUpdates.keySet().stream().map(String::valueOf).toArray(String[]::new))
            + ")";

    int updatedCount = entityManager.createNativeQuery(query).executeUpdate();

    LoggerFactory.db()
        .logUpdate(
            PROJECT_ENTITY,
            "진짜 배치 처리",
            "프로젝트 DB 조회수 배치 증가 완료. 처리된 프로젝트 수: " + updatedCount + ", 쿼리 수: 1개 (배치)");
  }
}
