package com.dataracy.modules.auth.infra.oauth;

import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;
import com.dataracy.modules.auth.infra.oauth.response.GoogleUserInfo;
import com.dataracy.modules.auth.infra.oauth.response.KakaoUserInfo;
import com.dataracy.modules.auth.status.AuthErrorStatus;
import com.dataracy.modules.auth.status.AuthException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    /**
     * OAuth2 사용자 정보 추출.
     * 어떤 구현체를 사용할지 결정한다.
     *
     * @param provider 소셜 유형 (구글, 카카오)
     * @param attributes 소셜 서버로부터 받은 유저 정보
     * @return 추출된 사용자 정보
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {

        return switch (provider) {
            case "google" -> new GoogleUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            default -> throw new AuthException(AuthErrorStatus.NOT_SUPPORTED_SOCIAL_TYPE);
        };
    }
}
