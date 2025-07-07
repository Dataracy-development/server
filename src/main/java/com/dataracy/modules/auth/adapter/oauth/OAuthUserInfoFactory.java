package com.dataracy.modules.auth.adapter.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.port.out.oauth.OAuthUserInfoPort;
import com.dataracy.modules.auth.application.port.out.oauth.SelectSocialProviderPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 스프링 빈으로 OAuthUserInfo인 클래스들이 adapters에 주입된다.
 * provider와 일치하는 소셜 로그인이 선택되어 유저 정보가 반환된다.
 */
@Component
@RequiredArgsConstructor
public class OAuthUserInfoFactory implements SelectSocialProviderPort {
    private final List<OAuthUserInfoPort> adapters;

    @Override
    public OAuthUserInfo extract(String provider, Map<String, Object> attributes) {
        return adapters.stream()
                .map(adapter -> adapter.extract(provider, attributes))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthErrorStatus.NOT_SUPPORTED_SOCIAL_PROVIDER_TYPE));
    }
}
