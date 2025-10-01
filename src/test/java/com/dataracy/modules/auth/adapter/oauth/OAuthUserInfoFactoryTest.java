package com.dataracy.modules.auth.adapter.oauth;

import com.dataracy.modules.auth.application.dto.response.OAuthUserInfo;
import com.dataracy.modules.auth.application.port.out.oauth.OAuthUserInfoPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
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

    /**
     * 예외 케이스 테스트 데이터를 제공합니다.
     */
    static Stream<TestCase> provideInvalidCases() {
        return Stream.of(
                new TestCase("unsupported", new HashMap<>(), "지원하지 않는 제공자"),
                new TestCase(null, new HashMap<>(), "null 제공자"),
                new TestCase("", new HashMap<>(), "빈 제공자"),
                new TestCase("google", null, "null 속성"),
                new TestCase("facebook", new HashMap<>(), "빈 속성 맵")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCases")
    @DisplayName("extract - 유효하지 않은 입력으로 호출하면 예외가 발생한다")
    void extract_WhenInvalidInput_ThrowsException(TestCase testCase) {
        // given
        when(googleAdapter.extract(any(), any())).thenReturn(null);
        when(kakaoAdapter.extract(any(), any())).thenReturn(null);

        // when & then
        AuthException exception = catchThrowableOfType(
                () -> oAuthUserInfoFactory.extract(testCase.provider, testCase.attributes),
                AuthException.class
        );
        assertAll(
                () -> assertThat(exception).isNotNull()
        );
    }

    /**
     * 테스트 케이스 레코드
     */
    record TestCase(String provider, Map<String, Object> attributes, String description) {
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
}
