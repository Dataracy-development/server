package com.dataracy.user.application.dto.response;

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
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return ((Map<String, Object>) attribute.get("kakao_account")).get("email").toString();
    }

    @Override
    public String getName() {
        return ((Map<String, Object>) attribute.get("properties")).get("nickname").toString();
    }
}
