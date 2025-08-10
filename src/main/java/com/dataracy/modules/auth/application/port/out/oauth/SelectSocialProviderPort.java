package com.dataracy.modules.auth.application.port.out.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;

import java.util.Map;

public interface SelectSocialProviderPort {
    /**
     * 프로바이더 선택
     * @param provider
     * @param attributes
     * @return
     */
    OAuthUserInfo extract(String provider, Map<String, Object> attributes);
}
