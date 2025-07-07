package com.dataracy.modules.auth.adapter.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.port.out.oauth.OAuthUserInfoPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * 제공자가 구글일 경우
 * 구글 유저 정보 저장
 */
@Component
public class GoogleOAuthUserInfoAdapter implements OAuthUserInfoPort {
    @Override
    public OAuthUserInfo extract(String provider, Map<String, Object> attributes) {
        if (!provider.equals("google")) return null;

        return new OAuthUserInfo(
                Optional.ofNullable(attributes.get("email")).map(Object::toString).orElse(null),
                Optional.ofNullable(attributes.get("name")).map(Object::toString).orElse(null),
                "google",
                Optional.ofNullable(attributes.get("sub")).map(Object::toString).orElse(null)
        );
    }
}
