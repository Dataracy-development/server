package com.dataracy.modules.dataset.adapter.query.sort;

import com.dataracy.modules.dataset.adapter.jpa.entity.QDataEntity;

import com.querydsl.core.types.dsl.NumberExpression;

public final class DataPopularOrderBuilder {
  private DataPopularOrderBuilder() {}

  /**
   * 인기 점수 계산식을 생성하여 반환합니다. 주어진 데이터 엔터티의 다운로드 수와 프로젝트 수를 가중치(각각 2.0, 1.5)를 곱해 합산한 인기 점수 표현식을 반환합니다.
   *
   * @param data 인기 점수를 계산할 데이터 엔터티
   * @param projectCountExpr 데이터 엔터티와 연관된 프로젝트 수를 나타내는 표현식
   * @return 다운로드 수와 연결된 프로젝트 수를 기반으로 계산된 인기 점수 표현식
   */
  public static NumberExpression<Double> popularScore(
      QDataEntity data, NumberExpression<Long> projectCountExpr) {
    NumberExpression<Double> projectScore = projectCountExpr.castToNum(Double.class).multiply(1.5);
    NumberExpression<Double> downloadScore =
        data.downloadCount.castToNum(Double.class).multiply(2.0);
    return downloadScore.add(projectScore);
  }
}
