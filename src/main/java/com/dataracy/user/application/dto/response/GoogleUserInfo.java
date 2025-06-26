package com.dataracy.user.application.dto.response;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attribute;
    
    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
