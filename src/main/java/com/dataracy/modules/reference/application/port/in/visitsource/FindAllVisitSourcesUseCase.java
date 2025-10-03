/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.port.in.visitsource;

import com.dataracy.modules.reference.application.dto.response.allview.AllVisitSourcesResponse;

public interface FindAllVisitSourcesUseCase {
  /**
   * 모든 방문 소스 정보를 조회하여 반환합니다.
   *
   * @return 전체 방문 소스 목록을 담은 AllVisitSourcesResponse 객체
   */
  AllVisitSourcesResponse findAllVisitSources();
}
