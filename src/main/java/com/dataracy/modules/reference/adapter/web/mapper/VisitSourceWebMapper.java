/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.adapter.web.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dataracy.modules.reference.adapter.web.response.allview.AllVisitSourcesWebResponse;
import com.dataracy.modules.reference.adapter.web.response.singleview.VisitSourceWebResponse;
import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;
import com.dataracy.modules.reference.application.dto.response.singleview.VisitSourceResponse;

/** visitSource 웹 DTO와 visitSource 애플리케이션 DTO를 변환하는 매퍼 */
@Component
public class VisitSourceWebMapper {
  /**
   * 애플리케이션 계층의 방문 소스 응답 DTO를 웹 계층의 방문 소스 응답 DTO로 변환합니다.
   *
   * @param visitSourceResponse 변환할 애플리케이션 방문 소스 응답 DTO
   * @return 변환된 웹 방문 소스 응답 DTO
   */
  public VisitSourceWebResponse toWebDto(VisitSourceResponse visitSourceResponse) {
    return new VisitSourceWebResponse(
        visitSourceResponse.id(), visitSourceResponse.value(), visitSourceResponse.label());
  }

  /**
   * 애플리케이션 전체 방문 경로 응답 DTO를 웹 전체 방문 경로 응답 DTO로 변환합니다.
   *
   * @param allVisitSourcesResponse 변환할 애플리케이션 전체 방문 경로 응답 DTO
   * @return 변환된 웹 전체 방문 경로 응답 DTO. 입력값이나 내부 리스트가 null인 경우 빈 리스트를 포함합니다.
   */
  public AllVisitSourcesWebResponse toWebDto(AllVisitSourcesResponse allVisitSourcesResponse) {
    if (allVisitSourcesResponse == null || allVisitSourcesResponse.visitSources() == null) {
      return new AllVisitSourcesWebResponse(List.of());
    }

    return new AllVisitSourcesWebResponse(
        allVisitSourcesResponse.visitSources().stream().map(this::toWebDto).toList());
  }
}
