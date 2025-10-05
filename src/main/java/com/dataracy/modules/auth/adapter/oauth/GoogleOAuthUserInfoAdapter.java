package com.dataracy.modules.auth.adapter.oauth;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.port.out.oauth.OAuthUserInfoPort;

@Component
public class GoogleOAuthUserInfoAdapter implements OAuthUserInfoPort {

  /**
   * 구글 서버로부터 받은 유저 정보를 추출하여 DTO로 변환한다.
   *
   * @param provider 소셜 제공자
   * @param attributes 소셜 제공자로부터 반환받은 속성 정보
   * @return 추출된 OAuth 사용자 정보
   */
  @Override
  public OAuthUserInfo extract(String provider, Map<String, Object> attributes) {
    if (!provider.equals("google")) return null;

    return new OAuthUserInfo(
        Optional.ofNullable(attributes.get("email")).map(Object::toString).orElse(null),
        Optional.ofNullable(attributes.get("name")).map(Object::toString).orElse(null),
        "google",
        Optional.ofNullable(attributes.get("sub")).map(Object::toString).orElse(null));
  }
}
