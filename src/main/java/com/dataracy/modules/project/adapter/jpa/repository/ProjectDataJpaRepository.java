/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.jpa.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dataracy.modules.project.adapter.jpa.entity.ProjectDataEntity;

public interface ProjectDataJpaRepository extends JpaRepository<ProjectDataEntity, Long> {
  /**
   * 지정된 프로젝트 ID에 연결된 모든 데이터 ID의 집합을 반환합니다.
   *
   * @param projectId 데이터 ID를 조회할 프로젝트의 ID
   * @return 해당 프로젝트에 속한 데이터 ID들의 집합
   */
  @Query("SELECT pd.dataId FROM ProjectDataEntity pd WHERE pd.project.id = :projectId")
  Set<Long> findDataIdsByProjectId(@Param("projectId") Long projectId);

  /**
   * 주어진 프로젝트 ID와 dataId 목록에 해당하는 ProjectDataEntity 레코드를 모두 삭제합니다.
   *
   * @param projectId 삭제할 프로젝트의 ID
   * @param dataIds 삭제할 dataId의 집합
   */
  @Modifying(clearAutomatically = true)
  @Query(
      "DELETE FROM ProjectDataEntity pd WHERE pd.project.id = :projectId AND pd.dataId IN :dataIds")
  void deleteByProjectIdAndDataIdIn(
      @Param("projectId") Long projectId, @Param("dataIds") Set<Long> dataIds);
}
