package com.dataracy.modules.project.adapter.query.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.dataracy.modules.project.adapter.jpa.entity.QProjectEntity.projectEntity;
import static org.assertj.core.api.Assertions.assertThat;

class ProjectFilterPredicateTest {

    @Test
    @DisplayName("notDeleted - 삭제되지 않은 프로젝트만 필터링하는 조건을 반환한다")
    void notDeleted_ReturnsNotDeletedCondition() {
        // when
        BooleanExpression result = ProjectFilterPredicate.notDeleted();

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("projectEntity.isDeleted = false");
    }

    @Test
    @DisplayName("projectIdEq - projectId가 null이 아닌 경우 해당 ID와 일치하는 조건을 반환한다")
    void projectIdEq_WhenProjectIdIsNotNull_ReturnsIdEqualsCondition() {
        // given
        Long projectId = 1L;

        // when
        BooleanExpression result = ProjectFilterPredicate.projectIdEq(projectId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("projectEntity.id = 1");
    }

    @Test
    @DisplayName("projectIdEq - projectId가 null인 경우 null을 반환한다")
    void projectIdEq_WhenProjectIdIsNull_ReturnsNull() {
        // when
        BooleanExpression result = ProjectFilterPredicate.projectIdEq(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("keywordContains - 키워드가 유효한 경우 제목에 포함되는 조건을 반환한다")
    void keywordContains_WhenKeywordIsValid_ReturnsContainsCondition() {
        // given
        String keyword = "test";

        // when
        BooleanExpression result = ProjectFilterPredicate.keywordContains(keyword);

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("projectEntity.title");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("keywordContains - 키워드가 null, 빈 문자열, 공백인 경우 null을 반환한다")
    void keywordContains_WhenKeywordIsInvalid_ReturnsNull(String keyword) {
        // when
        BooleanExpression result = ProjectFilterPredicate.keywordContains(keyword);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("topicIdEq - topicId가 null이 아닌 경우 해당 토픽 ID와 일치하는 조건을 반환한다")
    void topicIdEq_WhenTopicIdIsNotNull_ReturnsTopicIdEqualsCondition() {
        // given
        Long topicId = 1L;

        // when
        BooleanExpression result = ProjectFilterPredicate.topicIdEq(topicId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("projectEntity.topicId = 1");
    }

    @Test
    @DisplayName("topicIdEq - topicId가 null인 경우 null을 반환한다")
    void topicIdEq_WhenTopicIdIsNull_ReturnsNull() {
        // when
        BooleanExpression result = ProjectFilterPredicate.topicIdEq(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("authorLevelIdEq - authorLevelId가 null이 아닌 경우 해당 저자레벨 ID와 일치하는 조건을 반환한다")
    void authorLevelIdEq_WhenAuthorLevelIdIsNotNull_ReturnsAuthorLevelIdEqualsCondition() {
        // given
        Long authorLevelId = 1L;

        // when
        BooleanExpression result = ProjectFilterPredicate.authorLevelIdEq(authorLevelId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("projectEntity.authorLevelId = 1");
    }

    @Test
    @DisplayName("authorLevelIdEq - authorLevelId가 null인 경우 null을 반환한다")
    void authorLevelIdEq_WhenAuthorLevelIdIsNull_ReturnsNull() {
        // when
        BooleanExpression result = ProjectFilterPredicate.authorLevelIdEq(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("dataSourceIdEq - dataSourceId가 null이 아닌 경우 해당 데이터소스 ID와 일치하는 조건을 반환한다")
    void dataSourceIdEq_WhenDataSourceIdIsNotNull_ReturnsDataSourceIdEqualsCondition() {
        // given
        Long dataSourceId = 1L;

        // when
        BooleanExpression result = ProjectFilterPredicate.dataSourceIdEq(dataSourceId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.toString()).contains("projectEntity.dataSourceId = 1");
    }

    @Test
    @DisplayName("dataSourceIdEq - dataSourceId가 null인 경우 null을 반환한다")
    void dataSourceIdEq_WhenDataSourceIdIsNull_ReturnsNull() {
        // when
        BooleanExpression result = ProjectFilterPredicate.dataSourceIdEq(null);

        // then
        assertThat(result).isNull();
    }

}
