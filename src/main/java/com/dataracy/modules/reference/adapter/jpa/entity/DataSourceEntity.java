package com.dataracy.modules.reference.adapter.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
    name = "data_source",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"value"})})
public class DataSourceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "data_source_id")
  private Long id;

  @Column(nullable = false)
  private String value;

  @Column(nullable = false)
  private String label;

  /**
   * 주어진 value와 label로 새로운 DataSourceEntity 인스턴스를 생성합니다.
   *
   * @param value 데이터 소스의 값
   * @param label 데이터 소스의 라벨
   * @return value와 label이 설정된 DataSourceEntity 객체
   */
  public static DataSourceEntity of(String value, String label) {
    return DataSourceEntity.builder().value(value).label(label).build();
  }
}
