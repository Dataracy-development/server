package com.dataracy.modules.dataset.adapter.query.predicates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.querydsl.core.types.dsl.BooleanExpression;

class DataFilterPredicateTest {

  @Test
  @DisplayName("notDeleted - 삭제되지 않은 데이터만 필터링하는 조건을 반환한다")
  void notDeletedReturnsNotDeletedCondition() {
    // when
    BooleanExpression result = DataFilterPredicate.notDeleted();

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("dataEntity.isDeleted = false"));
  }

  @Test
  @DisplayName("dataIdEq - dataId가 null이 아닌 경우 해당 ID와 일치하는 조건을 반환한다")
  void dataIdEqWhenDataIdIsNotNullReturnsIdEqualsCondition() {
    // given
    Long dataId = 1L;

    // when
    BooleanExpression result = DataFilterPredicate.dataIdEq(dataId);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("dataEntity.id = 1"));
  }

  @Test
  @DisplayName("dataIdEq - dataId가 null인 경우 null을 반환한다")
  void dataIdEqWhenDataIdIsNullReturnsNull() {
    // when
    BooleanExpression result = DataFilterPredicate.dataIdEq(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("keywordContains - 키워드가 유효한 경우 제목 또는 설명에 포함되는 조건을 반환한다")
  void keywordContainsWhenKeywordIsValidReturnsContainsCondition() {
    // given
    String keyword = "test";

    // when
    BooleanExpression result = DataFilterPredicate.keywordContains(keyword);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("dataEntity.title"),
        () -> assertThat(result.toString()).contains("dataEntity.description"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "\t"})
  @DisplayName("keywordContains - 키워드가 null, 빈 문자열, 공백인 경우 null을 반환한다")
  void keywordContainsWhenKeywordIsInvalidReturnsNull(String keyword) {
    // when
    BooleanExpression result = DataFilterPredicate.keywordContains(keyword);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("topicIdEq - topicId가 null이 아닌 경우 해당 토픽 ID와 일치하는 조건을 반환한다")
  void topicIdEqWhenTopicIdIsNotNullReturnsTopicIdEqualsCondition() {
    // given
    Long topicId = 1L;

    // when
    BooleanExpression result = DataFilterPredicate.topicIdEq(topicId);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("dataEntity.topicId = 1"));
  }

  @Test
  @DisplayName("topicIdEq - topicId가 null인 경우 null을 반환한다")
  void topicIdEqWhenTopicIdIsNullReturnsNull() {
    // when
    BooleanExpression result = DataFilterPredicate.topicIdEq(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("dataSourceIdEq - dataSourceId가 null이 아닌 경우 해당 데이터소스 ID와 일치하는 조건을 반환한다")
  void dataSourceIdEqWhenDataSourceIdIsNotNullReturnsDataSourceIdEqualsCondition() {
    // given
    Long dataSourceId = 1L;

    // when
    BooleanExpression result = DataFilterPredicate.dataSourceIdEq(dataSourceId);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("dataEntity.dataSourceId = 1"));
  }

  @Test
  @DisplayName("dataSourceIdEq - dataSourceId가 null인 경우 null을 반환한다")
  void dataSourceIdEqWhenDataSourceIdIsNullReturnsNull() {
    // when
    BooleanExpression result = DataFilterPredicate.dataSourceIdEq(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("dataTypeIdEq - dataTypeId가 null이 아닌 경우 해당 데이터타입 ID와 일치하는 조건을 반환한다")
  void dataTypeIdEqWhenDataTypeIdIsNotNullReturnsDataTypeIdEqualsCondition() {
    // given
    Long dataTypeId = 1L;

    // when
    BooleanExpression result = DataFilterPredicate.dataTypeIdEq(dataTypeId);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("dataEntity.dataTypeId = 1"));
  }

  @Test
  @DisplayName("dataTypeIdEq - dataTypeId가 null인 경우 null을 반환한다")
  void dataTypeIdEqWhenDataTypeIdIsNullReturnsNull() {
    // when
    BooleanExpression result = DataFilterPredicate.dataTypeIdEq(null);

    // then
    assertThat(result).isNull();
  }
}
