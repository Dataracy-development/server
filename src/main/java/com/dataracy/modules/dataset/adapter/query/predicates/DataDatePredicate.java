package com.dataracy.modules.dataset.adapter.query.predicates;

import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;

public class DataDatePredicate {
    /**
     * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
     */
    private DataDatePredicate() {}

    private static final QDataEntity data = QDataEntity.dataEntity;

    /**
     * 지정한 연도가 데이터 엔터티의 시작일과 종료일 범위 내에 포함되는지 여부를 나타내는 QueryDSL 조건식을 생성합니다.
     *
     * @param year 포함 여부를 확인할 연도 (null일 경우 null 반환)
     * @return 연도가 시작일과 종료일 사이(포함)에 해당하는 엔터티를 필터링하는 BooleanExpression, 입력이 null이면 null 반환
     */
    public static BooleanExpression yearBetween(Integer year) {
        if (year == null) return null;

        NumberTemplate<Integer> startYear = Expressions.numberTemplate(Integer.class, "year({0})", data.startDate);
        NumberTemplate<Integer> endYear = Expressions.numberTemplate(Integer.class, "year({0})", data.endDate);

        return startYear.loe(year).and(endYear.goe(year));
    }
}
