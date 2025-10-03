/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.web.mapper.search;

import org.springframework.stereotype.Component;

import com.dataracy.modules.dataset.adapter.web.request.search.FilteringDataWebRequest;
import com.dataracy.modules.dataset.adapter.web.response.search.FilteredDataWebResponse;
import com.dataracy.modules.dataset.application.dto.request.search.FilteringDataRequest;
import com.dataracy.modules.dataset.application.dto.response.search.FilteredDataResponse;

/** 데이터 필터링 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼 */
@Component
public class DataFilterWebMapper {
  /**
   * FilteringDataWebRequest 웹 요청 DTO를 FilteringDataRequest 애플리케이션 도메인 DTO로 변환합니다.
   *
   * @param webRequest 데이터 필터링 조건이 포함된 웹 요청 DTO
   * @return 필터링 조건이 반영된 FilteringDataRequest 도메인 DTO
   */
  public FilteringDataRequest toApplicationDto(FilteringDataWebRequest webRequest) {
    return new FilteringDataRequest(
        webRequest.keyword(),
        webRequest.sortType(),
        webRequest.topicId(),
        webRequest.dataSourceId(),
        webRequest.dataTypeId(),
        webRequest.year());
  }

  /**
   * 애플리케이션 계층의 FilteredDataResponse를 웹 계층의 FilteredDataWebResponse로 변환합니다.
   *
   * <p>필드들을 1:1로 매핑하여 새로운 웹 응답 DTO를 생성합니다(생성자 파라미터 순서와 동일). 입력 객체가 null인 경우 NPE가 발생할 수 있으므로 호출자는
   * null이 아님을 보장해야 합니다.
   *
   * @param responseDto 변환할 애플리케이션 계층의 필터링 결과 DTO
   * @return 변환된 웹 계층의 FilteredDataWebResponse
   */
  public FilteredDataWebResponse toWebDto(FilteredDataResponse responseDto) {
    return new FilteredDataWebResponse(
        responseDto.id(),
        responseDto.title(),
        responseDto.creatorId(),
        responseDto.creatorName(),
        responseDto.userProfileImageUrl(),
        responseDto.topicLabel(),
        responseDto.dataSourceLabel(),
        responseDto.dataTypeLabel(),
        responseDto.startDate(),
        responseDto.endDate(),
        responseDto.description(),
        responseDto.dataThumbnailUrl(),
        responseDto.downloadCount(),
        responseDto.sizeBytes(),
        responseDto.rowCount(),
        responseDto.columnCount(),
        responseDto.createdAt(),
        responseDto.countConnectedProjects());
  }
}
