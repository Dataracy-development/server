package com.dataracy.modules.project.adapter.persistence.repository.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;

import static com.dataracy.modules.project.adapter.persistence.entity.QProjectEntity.projectEntity;

public class ProjectPredicateFactory {
    /**
 * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private ProjectPredicateFactory() {}

    /**
     * 주어진 프로젝트 ID와 일치하는 프로젝트 엔티티의 조건식을 반환합니다.
     *
     * @param projectId 비교할 프로젝트 ID
     * @return 프로젝트 ID가 일치하는 경우의 BooleanExpression, projectId가 null이면 null 반환
     */
    public static BooleanExpression projectIdEq(Long projectId) {
        return projectId == null ? null : projectEntity.id.eq(projectId);
    }
}
