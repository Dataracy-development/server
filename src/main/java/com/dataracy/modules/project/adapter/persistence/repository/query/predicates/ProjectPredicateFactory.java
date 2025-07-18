package com.dataracy.modules.project.adapter.persistence.repository.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;

import static com.dataracy.modules.project.adapter.persistence.entity.QProjectEntity.projectEntity;

public class ProjectPredicateFactory {
    private ProjectPredicateFactory() {}

    public static BooleanExpression projectIdEq(Long projectId) {
        return projectId == null ? null : projectEntity.id.eq(projectId);
    }
}
