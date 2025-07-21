package com.dataracy.modules.dataset.adapter.query.sort;

import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;

public final class DataPopularOrderBuilder {
    private DataPopularOrderBuilder() {}

    public static OrderSpecifier<?> popularOrder(QDataEntity data, QProjectDataEntity projectData) {
        // 연결된 프로젝트 개수 * 1.5
        NumberExpression<Double> projectCountScore = Expressions.numberTemplate(
                Double.class,
                "({0}) * 1.5",
                JPAExpressions
                        .select(projectData.count())
                        .from(projectData)
                        .where(projectData.dataId.eq(data.id))
        );

        // downloadCount (정수) → 실수로 캐스팅 후 * 2.0
        NumberExpression<Double> downloadScore = data.downloadCount.castToNum(Double.class).multiply(2.0);

        // 두 점수를 더해 최종 인기순 정렬
        return downloadScore.add(projectCountScore).desc();
    }
}
