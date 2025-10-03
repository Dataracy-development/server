/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.port.out.oauth;

import java.util.Map;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;

public interface SelectSocialProviderPort {
  /**
   * 어떤 소셜 로그인을 사용하였는지 모르기에 사용한 소셜에 맞게 유저정보를 저장한다.
   *
   * @param provider 서버 제공자
   * @param attributes 서버 제공자로부터 받은 유저 정보
   * @return 추출하여 변환한 유저 정보 DTO
   */
  OAuthUserInfo extract(String provider, Map<String, Object> attributes);
}
