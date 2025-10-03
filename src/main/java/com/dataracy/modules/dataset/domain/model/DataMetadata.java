/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.domain.model;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DataMetadata {
  private Long id;

  private Integer rowCount;
  private Integer columnCount;
  private String previewJson;

  /**
   * 주어진 값들로 DataMetadata 도메인 객체를 생성하여 반환합니다.
   *
   * @param id 메타데이터의 고유 식별자
   * @param rowCount 데이터의 행 개수
   * @param columnCount 데이터의 열 개수
   * @param previewJson 데이터 미리보기 정보를 담은 JSON 문자열
   * @return 생성된 DataMetadata 인스턴스
   */
  public static DataMetadata of(
      Long id, Integer rowCount, Integer columnCount, String previewJson) {
    return DataMetadata.builder()
        .id(id)
        .rowCount(rowCount)
        .columnCount(columnCount)
        .previewJson(previewJson)
        .build();
  }
}
