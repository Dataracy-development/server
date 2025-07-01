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
        Object providerId = attribute.get("sub");
        return providerId != null ? providerId.toString() : null;
    }

    @Override
    public String getEmail() {
        Object email = attribute.get("email");
        return email != null ? email.toString() : "default@gmail.com";
    }

    @Override
    public String getName() {
        Object name = attribute.get("name");
        return name != null ? name.toString() : null;
    }
}
