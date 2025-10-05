package com.dataracy.modules.auth.adapter.oauth;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.port.out.oauth.OAuthUserInfoPort;

@Component
public class KakaoOAuthUserInfoAdapter implements OAuthUserInfoPort {
  /**
   * 카카오 서버로부터 받은 유저 정보를 추출하여 DTO로 변환한다.
   *
   * @param provider 소셜 제공자
   * @param attributes 소셜 제공자로부터 반환받은 속성 정보
   * @return 추출된 OAuth 사용자 정보
   */
  @Override
  public OAuthUserInfo extract(String provider, Map<String, Object> attributes) {
    if (!provider.equals("kakao")) return null;

    @SuppressWarnings("unchecked")
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    @SuppressWarnings("unchecked")
    Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

    return new OAuthUserInfo(
        Optional.ofNullable(kakaoAccount.get("email")).map(Object::toString).orElse(null),
        Optional.ofNullable(properties.get("nickname")).map(Object::toString).orElse(null),
        "kakao",
        Optional.ofNullable(attributes.get("id")).map(Object::toString).orElse(null));
  }
}
