/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
    name = "analysis_purpose",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"value"})})
public class AnalysisPurposeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "analysis_purpose_id")
  private Long id;

  @Column(nullable = false)
  private String value;

  @Column(nullable = false)
  private String label;

  /**
   * 주어진 value와 label로 새로운 AnalysisPurposeEntity 인스턴스를 생성합니다.
   *
   * @param value 분석 목적의 고유 값
   * @param label 분석 목적의 표시 이름
   * @return 지정된 value와 label을 가진 AnalysisPurposeEntity 객체
   */
  public static AnalysisPurposeEntity of(String value, String label) {
    return AnalysisPurposeEntity.builder().value(value).label(label).build();
  }
}
