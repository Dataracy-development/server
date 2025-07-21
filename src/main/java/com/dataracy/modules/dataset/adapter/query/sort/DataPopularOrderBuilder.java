package com.dataracy.modules.dataset.adapter.query.sort;

import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.dataracy.modules.project.adapter.jpa.entity.QProjectDataEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;

public final class DataPopularOrderBuilder {
    /**
 * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private DataPopularOrderBuilder() {}

    /**
     * 데이터 엔티티의 인기도를 계산하여 내림차순 정렬 기준을 생성합니다.
     *
     * 인기도 점수는 연결된 프로젝트 개수에 1.5를 곱한 값과 다운로드 수(실수 변환 후 2.0을 곱함)를 합산하여 산출됩니다.
     *
     * @param data      인기도를 평가할 데이터 엔티티의 QueryDSL 객체
     * @param projectData 데이터와 연결된 프로젝트 엔티티의 QueryDSL 객체
     * @return          인기도 기준 내림차순 정렬을 위한 OrderSpecifier
     */
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
