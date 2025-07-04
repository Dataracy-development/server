package com.dataracy.modules.auth.adapter.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.port.out.OAuthUserInfoPort;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 제공자가 카카오일 경우
 * 카카오 유저 정보 저장
 */
@Component
public class KakaoOAuthUserInfoAdapter implements OAuthUserInfoPort {
    @Override
    public OAuthUserInfo extract(String provider, Map<String, Object> attributes) {
        if (!provider.equals("kakao")) return null;

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        return new OAuthUserInfo(
                (String) kakaoAccount.get("email"),
                (String) properties.get("nickname"),
                "kakao",
                attributes.get("id").toString()
        );
    }
}
