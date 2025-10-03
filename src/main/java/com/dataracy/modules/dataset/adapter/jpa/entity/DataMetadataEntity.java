/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.jpa.entity;

import com.dataracy.modules.dataset.domain.model.DataMetadata;

import jakarta.persistence.*;
import lombok.*;

/** data metadata 테이블 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "data_metadata")
public class DataMetadataEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "data_metadata_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "data_id", nullable = false)
  private DataEntity data;

  @Column(nullable = false)
  private Integer rowCount;

  @Column(nullable = false)
  private Integer columnCount;

  // 미리보기 (N행) JSON 문자열
  @Lob
  @Column(columnDefinition = "TEXT")
  private String previewJson;

  /**
   * 연관된 DataEntity를 설정하고, 해당 DataEntity의 메타데이터 참조를 현재 엔티티로 동기화합니다.
   *
   * @param data 연결할 DataEntity 인스턴스
   */
  public void updateData(DataEntity data) {
    this.data = data;
    data.updateMetadata(this);
  }

  /**
   * 주어진 {@link DataMetadata} 객체의 값을 사용하여 엔터티의 행 수, 열 수, 미리보기 JSON을 갱신합니다.
   *
   * @param metadata 동기화할 데이터 메타데이터 도메인 모델
   */
  public void updateFrom(DataMetadata metadata) {
    this.rowCount = metadata.getRowCount();
    this.columnCount = metadata.getColumnCount();
    this.previewJson = metadata.getPreviewJson();
  }

  /**
   * 행 수, 열 수, 미리보기 JSON 문자열을 기반으로 새로운 DataMetadataEntity 객체를 생성합니다.
   *
   * @param rowCount 데이터의 행 개수
   * @param columnCount 데이터의 열 개수
   * @param previewJson 데이터 미리보기 정보를 담은 JSON 문자열
   * @return 생성된 DataMetadataEntity 인스턴스
   */
  public static DataMetadataEntity of(Integer rowCount, Integer columnCount, String previewJson) {
    return DataMetadataEntity.builder()
        .rowCount(rowCount)
        .columnCount(columnCount)
        .previewJson(previewJson)
        .build();
  }
}
