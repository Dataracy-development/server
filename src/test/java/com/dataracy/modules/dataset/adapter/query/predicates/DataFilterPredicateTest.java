package com.dataracy.modules.dataset.adapter.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DataFilterPredicateTest {

    @Test
    @DisplayName("notDeleted - 삭제되지 않은 데이터만 필터링하는 조건을 반환한다")
    void notDeleted_ReturnsNotDeletedCondition() {
        // when
        BooleanExpression result = DataFilterPredicate.notDeleted();

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("dataEntity.isDeleted = false");
    }

    @Test
    @DisplayName("dataIdEq - dataId가 null이 아닌 경우 해당 ID와 일치하는 조건을 반환한다")
    void dataIdEq_WhenDataIdIsNotNull_ReturnsIdEqualsCondition() {
        // given
        Long dataId = 1L;

        // when
        BooleanExpression result = DataFilterPredicate.dataIdEq(dataId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("dataEntity.id = 1");
    }

    @Test
    @DisplayName("dataIdEq - dataId가 null인 경우 null을 반환한다")
    void dataIdEq_WhenDataIdIsNull_ReturnsNull() {
        // when
        BooleanExpression result = DataFilterPredicate.dataIdEq(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("keywordContains - 키워드가 유효한 경우 제목 또는 설명에 포함되는 조건을 반환한다")
    void keywordContains_WhenKeywordIsValid_ReturnsContainsCondition() {
        // given
        String keyword = "test";

        // when
        BooleanExpression result = DataFilterPredicate.keywordContains(keyword);

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("dataEntity.title");
        assertThat(result.toString()).contains("dataEntity.description");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("keywordContains - 키워드가 null, 빈 문자열, 공백인 경우 null을 반환한다")
    void keywordContains_WhenKeywordIsInvalid_ReturnsNull(String keyword) {
        // when
        BooleanExpression result = DataFilterPredicate.keywordContains(keyword);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("topicIdEq - topicId가 null이 아닌 경우 해당 토픽 ID와 일치하는 조건을 반환한다")
    void topicIdEq_WhenTopicIdIsNotNull_ReturnsTopicIdEqualsCondition() {
        // given
        Long topicId = 1L;

        // when
        BooleanExpression result = DataFilterPredicate.topicIdEq(topicId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("dataEntity.topicId = 1");
    }

    @Test
    @DisplayName("topicIdEq - topicId가 null인 경우 null을 반환한다")
    void topicIdEq_WhenTopicIdIsNull_ReturnsNull() {
        // when
        BooleanExpression result = DataFilterPredicate.topicIdEq(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("dataSourceIdEq - dataSourceId가 null이 아닌 경우 해당 데이터소스 ID와 일치하는 조건을 반환한다")
    void dataSourceIdEq_WhenDataSourceIdIsNotNull_ReturnsDataSourceIdEqualsCondition() {
        // given
        Long dataSourceId = 1L;

        // when
        BooleanExpression result = DataFilterPredicate.dataSourceIdEq(dataSourceId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("dataEntity.dataSourceId = 1");
    }

    @Test
    @DisplayName("dataSourceIdEq - dataSourceId가 null인 경우 null을 반환한다")
    void dataSourceIdEq_WhenDataSourceIdIsNull_ReturnsNull() {
        // when
        BooleanExpression result = DataFilterPredicate.dataSourceIdEq(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("dataTypeIdEq - dataTypeId가 null이 아닌 경우 해당 데이터타입 ID와 일치하는 조건을 반환한다")
    void dataTypeIdEq_WhenDataTypeIdIsNotNull_ReturnsDataTypeIdEqualsCondition() {
        // given
        Long dataTypeId = 1L;

        // when
        BooleanExpression result = DataFilterPredicate.dataTypeIdEq(dataTypeId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("dataEntity.dataTypeId = 1");
    }

    @Test
    @DisplayName("dataTypeIdEq - dataTypeId가 null인 경우 null을 반환한다")
    void dataTypeIdEq_WhenDataTypeIdIsNull_ReturnsNull() {
        // when
        BooleanExpression result = DataFilterPredicate.dataTypeIdEq(null);

        // then
        assertThat(result).isNull();
    }

}
