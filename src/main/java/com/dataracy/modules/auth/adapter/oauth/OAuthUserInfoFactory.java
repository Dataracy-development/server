package com.dataracy.modules.auth.adapter.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.port.out.oauth.OAuthUserInfoPort;
import com.dataracy.modules.auth.application.port.out.oauth.SelectSocialProviderPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OAuthUserInfoFactory implements SelectSocialProviderPort {
    private final List<OAuthUserInfoPort> adapters;

    /**
     * 주어진 소셜 제공자 이름과 속성 정보를 기반으로 적합한 OAuth 사용자 정보를 추출합니다.
     * 구글, 카카오 등 어떤 소셜 서버로부터 로그인을 진행하는지 모르기에 stream으로 각자 찾아 반환한다.
     *
     * @param provider 소셜 제공자 이름
     * @param attributes 소셜 제공자에서 제공한 사용자 속성 정보
     * @return 추출된 OAuth 사용자 정보
     * @throws AuthException 지원하지 않는 소셜 제공자인 경우 발생
     */
    @Override
    public OAuthUserInfo extract(String provider, Map<String, Object> attributes) {
        return adapters.stream()
                .map(adapter -> adapter.extract(provider, attributes))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> {
                    LoggerFactory.domain().logRuleViolation("ProviderType", "지원하지 않는 소셜 제공자입니다.");
                    return new AuthException(AuthErrorStatus.NOT_SUPPORTED_SOCIAL_PROVIDER_TYPE);
                });
    }
}
