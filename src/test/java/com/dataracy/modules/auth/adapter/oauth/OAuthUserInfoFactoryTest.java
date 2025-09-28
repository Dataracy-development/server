package com.dataracy.modules.auth.adapter.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.port.out.oauth.OAuthUserInfoPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuthUserInfoFactoryTest {

    @Mock
    private OAuthUserInfoPort googleAdapter;

    @Mock
    private OAuthUserInfoPort kakaoAdapter;

    private OAuthUserInfoFactory oAuthUserInfoFactory;

    @BeforeEach
    void setUp() {
        oAuthUserInfoFactory = new OAuthUserInfoFactory(List.of(googleAdapter, kakaoAdapter));
    }

    @Test
    @DisplayName("extract - 구글 제공자로 유효한 OAuth 사용자 정보를 추출한다")
    void extract_WhenGoogleProvider_ReturnsOAuthUserInfo() {
        // given
        String provider = "google";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("name", "Test User");
        attributes.put("sub", "123456789");

        OAuthUserInfo expectedUserInfo = new OAuthUserInfo(
                "test@example.com",
                "Test User",
                "google",
                "123456789"
        );

        when(googleAdapter.extract(provider, attributes)).thenReturn(expectedUserInfo);

        // when
        OAuthUserInfo result = oAuthUserInfoFactory.extract(provider, attributes);

        // then
        assertThat(result).isEqualTo(expectedUserInfo);
    }

    @Test
    @DisplayName("extract - 카카오 제공자로 유효한 OAuth 사용자 정보를 추출한다")
    void extract_WhenKakaoProvider_ReturnsOAuthUserInfo() {
        // given
        String provider = "kakao";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("kakao_account", new HashMap<>());
        attributes.put("properties", new HashMap<>());
        attributes.put("id", "123456789");

        OAuthUserInfo expectedUserInfo = new OAuthUserInfo(
                "test@example.com",
                "Test User",
                "kakao",
                "123456789"
        );

        when(kakaoAdapter.extract(provider, attributes)).thenReturn(expectedUserInfo);

        // when
        OAuthUserInfo result = oAuthUserInfoFactory.extract(provider, attributes);

        // then
        assertThat(result).isEqualTo(expectedUserInfo);
    }

    @Test
    @DisplayName("extract - 지원하지 않는 제공자로 호출하면 예외가 발생한다")
    void extract_WhenUnsupportedProvider_ThrowsException() {
        // given
        String provider = "unsupported";
        Map<String, Object> attributes = new HashMap<>();

        when(googleAdapter.extract(provider, attributes)).thenReturn(null);
        when(kakaoAdapter.extract(provider, attributes)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> oAuthUserInfoFactory.extract(provider, attributes))
                .isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("extract - null 제공자로 호출하면 예외가 발생한다")
    void extract_WhenNullProvider_ThrowsException() {
        // given
        String provider = null;
        Map<String, Object> attributes = new HashMap<>();

        when(googleAdapter.extract(provider, attributes)).thenReturn(null);
        when(kakaoAdapter.extract(provider, attributes)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> oAuthUserInfoFactory.extract(provider, attributes))
                .isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("extract - 빈 제공자로 호출하면 예외가 발생한다")
    void extract_WhenEmptyProvider_ThrowsException() {
        // given
        String provider = "";
        Map<String, Object> attributes = new HashMap<>();

        when(googleAdapter.extract(provider, attributes)).thenReturn(null);
        when(kakaoAdapter.extract(provider, attributes)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> oAuthUserInfoFactory.extract(provider, attributes))
                .isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("extract - null 속성으로 호출하면 예외가 발생한다")
    void extract_WhenNullAttributes_ThrowsException() {
        // given
        String provider = "google";
        Map<String, Object> attributes = null;

        when(googleAdapter.extract(provider, attributes)).thenReturn(null);
        when(kakaoAdapter.extract(provider, attributes)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> oAuthUserInfoFactory.extract(provider, attributes))
                .isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("extract - 빈 속성 맵으로 호출하면 예외가 발생한다")
    void extract_WhenEmptyAttributes_ThrowsException() {
        // given
        String provider = "facebook";
        Map<String, Object> attributes = new HashMap<>();

        when(googleAdapter.extract(provider, attributes)).thenReturn(null);
        when(kakaoAdapter.extract(provider, attributes)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> oAuthUserInfoFactory.extract(provider, attributes))
                .isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("extract - 첫 번째 어댑터에서 null을 반환하고 두 번째 어댑터에서 유효한 정보를 반환한다")
    void extract_WhenFirstAdapterReturnsNullAndSecondReturnsValid_ReturnsValidInfo() {
        // given
        String provider = "kakao";
        Map<String, Object> attributes = new HashMap<>();

        OAuthUserInfo expectedUserInfo = new OAuthUserInfo(
                "test@example.com",
                "Test User",
                "kakao",
                "123456789"
        );

        when(kakaoAdapter.extract(provider, attributes)).thenReturn(expectedUserInfo);

        // when
        OAuthUserInfo result = oAuthUserInfoFactory.extract(provider, attributes);

        // then
        assertThat(result).isEqualTo(expectedUserInfo);
    }

    @Test
    @DisplayName("extract - 모든 어댑터에서 null을 반환하면 예외가 발생한다")
    void extract_WhenAllAdaptersReturnNull_ThrowsException() {
        // given
        String provider = "unknown";
        Map<String, Object> attributes = new HashMap<>();

        when(googleAdapter.extract(provider, attributes)).thenReturn(null);
        when(kakaoAdapter.extract(provider, attributes)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> oAuthUserInfoFactory.extract(provider, attributes))
                .isInstanceOf(AuthException.class);
    }
}
