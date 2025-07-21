package com.dataracy.modules.data.adapter.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.StringUtils;

import static com.dataracy.modules.data.adapter.jpa.entity.QDataEntity.dataEntity;

public class DataFilterPredicate {
    /**
 * 이 클래스의 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private DataFilterPredicate() {}

    public static BooleanExpression dataIdEq(Long dataId) {
        return dataId == null ? null : dataEntity.id.eq(dataId);
    }

    public static BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;

        return dataEntity.title.containsIgnoreCase(keyword)
                .or(dataEntity.description.containsIgnoreCase(keyword));
    }

    public static BooleanExpression topicIdEq(Long topicId) {
        return topicId == null ? null : dataEntity.topicId.eq(topicId);
    }

    public static BooleanExpression dataTypeIdEq(Long dataTypeId) {
        return dataTypeId == null ? null : dataEntity.dataTypeId.eq(dataTypeId);
    }
}
