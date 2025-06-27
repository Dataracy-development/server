package com.dataracy.modules.auth.infra.oauth;

import com.dataracy.modules.auth.status.AuthErrorStatus;
import com.dataracy.modules.auth.status.AuthException;
import com.dataracy.modules.auth.infra.oauth.response.GoogleUserInfo;
import com.dataracy.modules.auth.infra.oauth.response.KakaoUserInfo;
import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attribute) {
        return switch (provider) {
            case "google" -> new GoogleUserInfo(attribute);
            case "kakao" -> new KakaoUserInfo(attribute);
            default -> throw new AuthException(AuthErrorStatus.NOT_SUPPORTED_SOCIAL_TYPE);
        };
    }
}
