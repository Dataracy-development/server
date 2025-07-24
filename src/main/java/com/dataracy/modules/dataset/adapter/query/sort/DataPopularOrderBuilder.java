package com.dataracy.modules.dataset.adapter.query.sort;

import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;
import com.querydsl.core.types.dsl.NumberExpression;

public final class DataPopularOrderBuilder {
    /**
 * 인스턴스 생성을 방지하기 위한 private 생성자입니다.
 */
private DataPopularOrderBuilder() {}

    public static NumberExpression<Double> popularScore(QDataEntity data, NumberExpression<Long> projectCountExpr) {
        NumberExpression<Double> projectScore = projectCountExpr.castToNum(Double.class).multiply(1.5);
        NumberExpression<Double> downloadScore = data.downloadCount.castToNum(Double.class).multiply(2.0);
        return downloadScore.add(projectScore);
    }
}
