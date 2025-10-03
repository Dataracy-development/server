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
    name = "occupation",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"value"})})
public class OccupationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "occupation_id")
  private Long id;

  @Column(nullable = false)
  private String value;

  @Column(nullable = false)
  private String label;

  /**
   * 주어진 value와 label을 사용하여 새로운 OccupationEntity 인스턴스를 생성합니다.
   *
   * @param value 직업의 고유 값
   * @param label 직업의 표시 이름
   * @return 생성된 OccupationEntity 객체
   */
  public static OccupationEntity of(String value, String label) {
    return OccupationEntity.builder().value(value).label(label).build();
  }
}
