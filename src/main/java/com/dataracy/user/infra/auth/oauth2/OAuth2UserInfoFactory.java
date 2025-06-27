package com.dataracy.user.infra.auth.oauth2;

import com.dataracy.user.infra.auth.oauth2.dto.GoogleUserInfo;
import com.dataracy.user.infra.auth.oauth2.dto.KakaoUserInfo;
import com.dataracy.user.infra.auth.oauth2.dto.OAuth2UserInfo;
import com.dataracy.user.status.AuthErrorStatus;
import com.dataracy.user.status.AuthException;

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
