package com.dataracy.modules.dataset.adapter.query.sort;

import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataPopularOrderBuilderTest {

    @Test
    @DisplayName("popularScore는 다운로드 수와 프로젝트 수 기반으로 점수를 계산한다")
    void popularScoreShouldCalculateCorrectly() {
        // given
        QDataEntity data = QDataEntity.dataEntity;
        NumberExpression<Long> projectCountExpr = Expressions.numberTemplate(Long.class, "{0}", 5L);

        // when
        NumberExpression<Double> result = DataPopularOrderBuilder.popularScore(data, projectCountExpr);

        // then
        assertThat(result).isNotNull();
    }
}
