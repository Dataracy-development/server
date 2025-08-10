package com.dataracy.modules.auth.adapter.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.port.out.oauth.OAuthUserInfoPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class GoogleOAuthUserInfoAdapter implements OAuthUserInfoPort {

    /**
     *
     * @param provider
     * @param attributes
     * @return
     */
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
