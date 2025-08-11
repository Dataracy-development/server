package com.dataracy.modules.dataset.adapter.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.StringUtils;

import static com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity.dataEntity;

public class DataFilterPredicate {
    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private DataFilterPredicate() {}

    /**
     * 주어진 데이터 ID와 일치하는 데이터 엔터티를 필터링하는 QueryDSL 조건식을 반환합니다.
     *
     * @param dataId 필터링할 데이터 엔터티의 ID
     * @return 데이터 ID가 일치하는 경우의 BooleanExpression, dataId가 null이면 null 반환
     */
    public static BooleanExpression dataIdEq(Long dataId) {
        return dataId == null ? null : dataEntity.id.eq(dataId);
    }

    /**
     * 주어진 키워드가 데이터 엔티티의 제목 또는 설명에(대소문자 구분 없이) 포함되어 있는지 확인하는 QueryDSL 조건식을 반환합니다.
     *
     * @param keyword 검색할 키워드
     * @return 제목 또는 설명에 키워드가 포함되어 있으면 해당 조건식, 키워드가 비어 있거나 null이면 null
     */
    public static BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;

        return dataEntity.title.containsIgnoreCase(keyword)
                .or(dataEntity.description.containsIgnoreCase(keyword));
    }

    /**
     * 주어진 topicId와 일치하는 데이터 엔터티만 필터링하는 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param topicId 필터링할 토픽 ID
     * @return topicId가 null이 아니면 해당 topicId와 일치하는 조건의 BooleanExpression, null이면 null 반환
     */
    public static BooleanExpression topicIdEq(Long topicId) {
        return topicId == null ? null : dataEntity.topicId.eq(topicId);
    }

    /**
     * 주어진 dataSourceId와 일치하는 데이터 엔터티만 필터링하는 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param dataSourceId 필터링할 데이터 소스의 ID
     * @return dataSourceId가 일치하는 경우에만 true인 BooleanExpression, dataSourceId가 null이면 null 반환
     */
    public static BooleanExpression dataSourceIdEq(Long dataSourceId) {
        return dataSourceId == null ? null : dataEntity.dataSourceId.eq(dataSourceId);
    }

    /**
     * 주어진 데이터 타입 ID와 일치하는 데이터 엔티티만 필터링하는 QueryDSL BooleanExpression을 반환합니다.
     *
     * @param dataTypeId 필터링할 데이터 타입 ID (null이면 조건식이 생성되지 않음)
     * @return 데이터 타입 ID가 일치하는 경우의 BooleanExpression, 또는 dataTypeId가 null이면 null
     */
    public static BooleanExpression dataTypeIdEq(Long dataTypeId) {
        return dataTypeId == null ? null : dataEntity.dataTypeId.eq(dataTypeId);
    }
}
