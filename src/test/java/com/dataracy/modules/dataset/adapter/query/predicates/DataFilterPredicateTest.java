package com.dataracy.modules.dataset.adapter.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataFilterPredicateTest {

    @Test
    @DisplayName("dataId가 null이면 null을 반환한다")
    void dataIdEqShouldReturnNullWhenIdIsNull() {
        // when
        BooleanExpression result = DataFilterPredicate.dataIdEq(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("keyword가 null이면 null을 반환한다")
    void keywordContainsShouldReturnNullWhenKeywordIsNull() {
        // when
        BooleanExpression result = DataFilterPredicate.keywordContains(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("keyword가 주어지면 조건식을 반환한다")
    void keywordContainsShouldReturnExpression() {
        // when
        BooleanExpression result = DataFilterPredicate.keywordContains("test");

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("dataIdIn이 null이면 null을 반환한다")
    void dataIdInShouldReturnNullWhenIdsIsNull() {
        // when
        BooleanExpression result = DataFilterPredicate.dataIdIn(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("dataIdIn이 비어있으면 null을 반환한다")
    void dataIdInShouldReturnNullWhenIdsEmpty() {
        // when
        BooleanExpression result = DataFilterPredicate.dataIdIn(List.of());

        // then
        assertThat(result).isNull();
    }
}
