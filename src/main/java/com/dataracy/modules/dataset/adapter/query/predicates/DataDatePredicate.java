package com.dataracy.modules.dataset.adapter.query.predicates;

import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;

public class DataDatePredicate {
    private static final QDataEntity data = QDataEntity.dataEntity;

    public static BooleanExpression yearBetween(Integer year) {
        if (year == null) return null;

        NumberTemplate<Integer> startYear = Expressions.numberTemplate(Integer.class, "year({0})", data.startDate);
        NumberTemplate<Integer> endYear = Expressions.numberTemplate(Integer.class, "year({0})", data.endDate);

        return startYear.loe(year).and(endYear.goe(year));
    }
}
