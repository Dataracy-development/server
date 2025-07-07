package com.dataracy.modules.auth.application.port.out.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;

import java.util.Map;

/**
 * 소셜 서버들로부터 어느 제공자로부터 정보를 받았는지 확인
 * 받은 정보를 각자의 응답 형태로부터 공통된 응답형태인 OAuthUserInfo로 변환한다.
 */
public interface SelectSocialProviderPort {
    OAuthUserInfo extract(String provider, Map<String, Object> attributes);
}
