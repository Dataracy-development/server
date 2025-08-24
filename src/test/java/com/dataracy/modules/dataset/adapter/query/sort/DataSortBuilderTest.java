package com.dataracy.modules.dataset.adapter.query.sort;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataSortBuilderTest {

    @Test
    @DisplayName("sortType이 null이면 최신순으로 정렬한다")
    void fromSortOptionShouldReturnLatestWhenNull() {
        // given
        NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");

        // when
        OrderSpecifier<?>[] result = DataSortBuilder.fromSortOption(null, projectCountPath);

        // then
        assertThat(result).hasSize(1);
        assertThat(result[0].getTarget().toString()).contains("createdAt"); // 정렬 대상 확인
        assertThat(result[0].getOrder()).isEqualTo(Order.DESC);             // 내림차순 확인
    }

    @Test
    @DisplayName("UTILIZE 정렬은 projectCountPath 기준 내림차순이다")
    void fromSortOptionShouldUseProjectCountWhenUtilize() {
        // given
        NumberPath<Long> projectCountPath = Expressions.numberPath(Long.class, "projectCount");

        // when
        OrderSpecifier<?>[] result = DataSortBuilder.fromSortOption(
                com.dataracy.modules.dataset.domain.enums.DataSortType.UTILIZE,
                projectCountPath
        );

        // then
        assertThat(result).hasSize(1);
        assertThat(result[0].getTarget().toString()).contains("projectCount");
        assertThat(result[0].getOrder()).isEqualTo(Order.DESC);
    }
}
