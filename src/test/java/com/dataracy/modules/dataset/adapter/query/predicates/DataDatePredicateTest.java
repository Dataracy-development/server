package com.dataracy.modules.dataset.adapter.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataDatePredicateTest {

    @Test
    @DisplayName("year가 null이면 null을 반환한다")
    void yearBetweenShouldReturnNullWhenYearIsNull() {
        // given
        Integer year = null;

        // when
        BooleanExpression result = DataDatePredicate.yearBetween(year);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("year가 주어지면 조건식을 반환한다")
    void yearBetweenShouldReturnExpressionWhenYearProvided() {
        // given
        Integer year = 2023;

        // when
        BooleanExpression result = DataDatePredicate.yearBetween(year);

        // then
        assertThat(result).isNotNull();
    }
}
