/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.project.adapter.query.predicates;

import static com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity.projectDataEntity;

import com.querydsl.core.types.dsl.BooleanExpression;

public final class ProjectDataFilterPredicate {
  private ProjectDataFilterPredicate() {}

  /**
   * 프로젝트가 삭제되지 않은 경우에 해당하는 필터 조건을 반환합니다.
   *
   * @return 프로젝트의 isDeleted 플래그가 false인 경우를 나타내는 BooleanExpression
   */
  public static BooleanExpression notDeleted() {
    return projectDataEntity.project.isDeleted.isFalse();
  }

  /**
   * 주어진 프로젝트 ID와 일치하는 프로젝트 데이터 엔티티를 필터링하는 QueryDSL BooleanExpression을 반환합니다.
   *
   * @param projectId 필터링할 프로젝트의 ID. null인 경우 null을 반환합니다.
   * @return 프로젝트 ID가 일치할 때 true인 BooleanExpression, 또는 projectId가 null이면 null
   */
  public static BooleanExpression projectIdEq(Long projectId) {
    return projectId == null ? null : projectDataEntity.project.id.eq(projectId);
  }

  /**
   * 주어진 dataId와 일치하는 프로젝트 데이터 엔터티의 dataId에 대한 QueryDSL BooleanExpression을 반환합니다.
   *
   * @param dataId 필터링할 데이터 ID
   * @return dataId가 null이 아니면 해당 dataId와 일치하는 조건의 BooleanExpression, null이면 null 반환
   */
  public static BooleanExpression dataIdEq(Long dataId) {
    return dataId == null ? null : projectDataEntity.dataId.eq(dataId);
  }
}
