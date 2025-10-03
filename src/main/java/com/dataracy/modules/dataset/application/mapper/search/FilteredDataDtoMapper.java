/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.mapper.search;

import org.springframework.stereotype.Component;

import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;
import com.dataracy.modules.dataset.domain.model.Data;

/** 필터링된 데이터셋 도메인 DTO와 필터링된 데이터셋 도메인 모델을 변환하는 매퍼 */
@Component
public class FilteredDataDtoMapper {
  /**
   * Data 도메인과 제공된 라벨·메타정보를 조합해 FilteredDataResponse DTO를 생성하여 반환합니다.
   *
   * <p>생성되는 DTO에는 데이터의 식별자, 제목, 업로더 정보(username, userProfileImageUrl), 주제·소스·유형 라벨, 시작/종료일, 설명, 썸네일
   * URL, 다운로드 수, 크기(bytes), 메타데이터(row/column), 생성일(createdAt) 및 연결된 프로젝트 수(countConnectedProjects)가
   * 포함됩니다.
   *
   * @param data 변환 대상 Data 도메인 객체
   * @param username 업로더의 표시 이름(닉네임)
   * @param userProfileImageUrl 업로더의 프로필 이미지 URL
   * @param topicLabel 데이터의 주제 라벨
   * @param dataSourceLabel 데이터 소스 라벨
   * @param dataTypeLabel 데이터 유형 라벨
   * @param countConnectedProjects 해당 데이터와 연결된 프로젝트 수
   * @return 구성된 FilteredDataResponse DTO
   */
  public FilteredDataResponse toResponseDto(
      Data data,
      String username,
      String userProfileImageUrl,
      String topicLabel,
      String dataSourceLabel,
      String dataTypeLabel,
      Long countConnectedProjects) {
    return new FilteredDataResponse(
        data.getId(),
        data.getTitle(),
        data.getUserId(),
        username,
        userProfileImageUrl,
        topicLabel,
        dataSourceLabel,
        dataTypeLabel,
        data.getStartDate(),
        data.getEndDate(),
        data.getDescription(),
        data.getDataThumbnailUrl(),
        data.getDownloadCount(),
        data.getSizeBytes(),
        data.getMetadata().getRowCount(),
        data.getMetadata().getColumnCount(),
        data.getCreatedAt(),
        countConnectedProjects);
  }
}
