package com.dataracy.modules.project.adapter.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.StringUtils;

import static com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity.projectEntity;

public class ProjectFilterPredicate {
    private ProjectFilterPredicate() {}

    public static BooleanExpression projectIdEq(Long projectId) {
        return projectId == null ? null : projectEntity.id.eq(projectId);
    }

    public static BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;

        return projectEntity.title.containsIgnoreCase(keyword);
    }


    public static BooleanExpression topicIdEq(Long topicId) {
        return topicId == null ? null : projectEntity.topicId.eq(topicId);
    }

    public static BooleanExpression analysisPurposeIdEq(Long analysisPurposeId) {
        return analysisPurposeId == null ? null : projectEntity.analysisPurposeId.eq(analysisPurposeId);
    }

    public static BooleanExpression dataSourceIdEq(Long dataSourceId) {
        return dataSourceId == null ? null : projectEntity.dataSourceId.eq(dataSourceId);
    }

    public static BooleanExpression authorLevelIdEq(Long authorLevelId) {
        return authorLevelId == null ? null : projectEntity.authorLevelId.eq(authorLevelId);
    }
}
