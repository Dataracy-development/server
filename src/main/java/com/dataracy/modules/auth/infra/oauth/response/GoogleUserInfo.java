package com.dataracy.modules.auth.infra.oauth.response;

import com.dataracy.modules.auth.domain.model.OAuth2UserInfo;
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
        Object email = attribute.get("email");
        return email != null ? email.toString() : "blank@example.com";
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
