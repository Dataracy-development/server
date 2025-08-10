package com.dataracy.modules.auth.adapter.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.port.out.oauth.OAuthUserInfoPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class KakaoOAuthUserInfoAdapter implements OAuthUserInfoPort {
    /**
     *
     * @param provider
     * @param attributes
     * @return
     */
    @Override
    public OAuthUserInfo extract(String provider, Map<String, Object> attributes) {
        if (!provider.equals("kakao")) return null;

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        return new OAuthUserInfo(
                Optional.ofNullable(kakaoAccount.get("email")).map(Object::toString).orElse(null),
                Optional.ofNullable(properties.get("nickname")).map(Object::toString).orElse(null),
                "kakao",
                Optional.ofNullable(attributes.get("id")).map(Object::toString).orElse(null)
        );
    }
}
