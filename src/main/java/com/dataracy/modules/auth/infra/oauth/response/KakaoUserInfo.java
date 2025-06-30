package com.dataracy.modules.auth.infra.oauth.response;

import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attribute;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        Object providerId = attribute.get("id");
        return providerId != null ? providerId.toString() : null;
    }

    @Override
    public String getEmail() {
        Object email = ((Map<String, Object>) attribute.get("kakao_account")).get("email");
        return email != null ? email.toString() : "default@gmail.com";
    }

    @Override
    public String getName() {
        Object name = ((Map<String, Object>) attribute.get("properties")).get("nickname");
        return name != null ? name.toString() : null;
    }
}
