package com.dataracy.modules.project.adapter.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;

import static com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity.projectDataEntity;

public class ProjectDataFilterPredicate {
    /**
 * 이 클래스의 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private ProjectDataFilterPredicate() {}

    public static BooleanExpression projectIdEq(Long projectId) {
        return projectId == null ? null : projectDataEntity.project.id.eq(projectId);
    }

    public static BooleanExpression projectIdNumberPathEq(NumberPath<Long> projectId) {
        return projectId == null ? null : projectDataEntity.project.id.eq(projectId);
    }

    public static BooleanExpression dataIdEq(Long dataId) {
        return dataId == null ? null : projectDataEntity.dataId.eq(dataId);
    }
}
