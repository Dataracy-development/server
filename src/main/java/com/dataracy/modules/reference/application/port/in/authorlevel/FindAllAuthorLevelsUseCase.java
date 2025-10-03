/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.reference.application.port.in.authorlevel;

import com.dataracy.modules.reference.application.dto.response.allview.AllAuthorLevelsResponse;

public interface FindAllAuthorLevelsUseCase {
  /**
   * 모든 저자 레벨 정보를 반환합니다.
   *
   * @return 전체 저자 레벨 정보를 포함하는 응답 객체
   */
  AllAuthorLevelsResponse findAllAuthorLevels();
}
