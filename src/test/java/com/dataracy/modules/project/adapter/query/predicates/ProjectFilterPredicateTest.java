package com.dataracy.modules.project.adapter.query.predicates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.querydsl.core.types.dsl.BooleanExpression;

class ProjectFilterPredicateTest {

  @Test
  @DisplayName("notDeleted - 삭제되지 않은 프로젝트만 필터링하는 조건을 반환한다")
  void notDeletedReturnsNotDeletedCondition() {
    // when
    BooleanExpression result = ProjectFilterPredicate.notDeleted();

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("projectEntity.isDeleted = false"));
  }

  @Test
  @DisplayName("projectIdEq - projectId가 null이 아닌 경우 해당 ID와 일치하는 조건을 반환한다")
  void projectIdEqWhenProjectIdIsNotNullReturnsIdEqualsCondition() {
    // given
    Long projectId = 1L;

    // when
    BooleanExpression result = ProjectFilterPredicate.projectIdEq(projectId);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("projectEntity.id = 1"));
  }

  @Test
  @DisplayName("projectIdEq - projectId가 null인 경우 null을 반환한다")
  void projectIdEqWhenProjectIdIsNullReturnsNull() {
    // when
    BooleanExpression result = ProjectFilterPredicate.projectIdEq(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("keywordContains - 키워드가 유효한 경우 제목에 포함되는 조건을 반환한다")
  void keywordContainsWhenKeywordIsValidReturnsContainsCondition() {
    // given
    String keyword = "test";

    // when
    BooleanExpression result = ProjectFilterPredicate.keywordContains(keyword);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("projectEntity.title"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "\t"})
  @DisplayName("keywordContains - 키워드가 null, 빈 문자열, 공백인 경우 null을 반환한다")
  void keywordContainsWhenKeywordIsInvalidReturnsNull(String keyword) {
    // when
    BooleanExpression result = ProjectFilterPredicate.keywordContains(keyword);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("topicIdEq - topicId가 null이 아닌 경우 해당 토픽 ID와 일치하는 조건을 반환한다")
  void topicIdEqWhenTopicIdIsNotNullReturnsTopicIdEqualsCondition() {
    // given
    Long topicId = 1L;

    // when
    BooleanExpression result = ProjectFilterPredicate.topicIdEq(topicId);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("projectEntity.topicId = 1"));
  }

  @Test
  @DisplayName("topicIdEq - topicId가 null인 경우 null을 반환한다")
  void topicIdEqWhenTopicIdIsNullReturnsNull() {
    // when
    BooleanExpression result = ProjectFilterPredicate.topicIdEq(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("authorLevelIdEq - authorLevelId가 null이 아닌 경우 해당 저자레벨 ID와 일치하는 조건을 반환한다")
  void authorLevelIdEqWhenAuthorLevelIdIsNotNullReturnsAuthorLevelIdEqualsCondition() {
    // given
    Long authorLevelId = 1L;

    // when
    BooleanExpression result = ProjectFilterPredicate.authorLevelIdEq(authorLevelId);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("projectEntity.authorLevelId = 1"));
  }

  @Test
  @DisplayName("authorLevelIdEq - authorLevelId가 null인 경우 null을 반환한다")
  void authorLevelIdEqWhenAuthorLevelIdIsNullReturnsNull() {
    // when
    BooleanExpression result = ProjectFilterPredicate.authorLevelIdEq(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("dataSourceIdEq - dataSourceId가 null이 아닌 경우 해당 데이터소스 ID와 일치하는 조건을 반환한다")
  void dataSourceIdEqWhenDataSourceIdIsNotNullReturnsDataSourceIdEqualsCondition() {
    // given
    Long dataSourceId = 1L;

    // when
    BooleanExpression result = ProjectFilterPredicate.dataSourceIdEq(dataSourceId);

    // then
    assertAll(
        () -> assertThat(result).isNotNull(),
        () -> assertThat(result.toString()).contains("projectEntity.dataSourceId = 1"));
  }

  @Test
  @DisplayName("dataSourceIdEq - dataSourceId가 null인 경우 null을 반환한다")
  void dataSourceIdEqWhenDataSourceIdIsNullReturnsNull() {
    // when
    BooleanExpression result = ProjectFilterPredicate.dataSourceIdEq(null);

    // then
    assertThat(result).isNull();
  }
}
