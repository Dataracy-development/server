package com.dataracy.modules.auth.infra.oauth;

import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;
import com.dataracy.modules.auth.infra.oauth.response.GoogleUserInfo;
import com.dataracy.modules.auth.infra.oauth.response.KakaoUserInfo;
import com.dataracy.modules.auth.status.AuthErrorStatus;
import com.dataracy.modules.auth.status.AuthException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Map;

public class OAuth2UserInfoFactory {

    /**
     * OAuth2 사용자 정보 추출.
     *
     * @param token OAuth2 인증 토큰
     * @return 추출된 사용자 정보
     */
    public static OAuth2UserInfo getOAuth2UserInfo(OAuth2AuthenticationToken token) {

        String provider = token.getAuthorizedClientRegistrationId();
        Map<String, Object> attributes = token.getPrincipal().getAttributes();

        return switch (provider) {
            case "google" -> new GoogleUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            default -> throw new AuthException(AuthErrorStatus.NOT_SUPPORTED_SOCIAL_TYPE);
        };
    }
}
