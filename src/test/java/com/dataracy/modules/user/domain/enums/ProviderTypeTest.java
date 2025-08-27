package com.dataracy.modules.user.domain.enums;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ProviderTypeTest {

    @Test
    @DisplayName("of: 대소문자 무시한 매칭(LOCAL/KAKAO/GOOGLE)")
    void ofShouldReturnEnumWhenValid() {
        // given
        String s1 = "local";
        String s2 = "KAKAO";
        String s3 = "GoOgLe";

        // when
        ProviderType local = ProviderType.of(s1);
        ProviderType kakao = ProviderType.of(s2);
        ProviderType google = ProviderType.of(s3);

        // then
        assertThat(local).isEqualTo(ProviderType.LOCAL);
        assertThat(kakao).isEqualTo(ProviderType.KAKAO);
        assertThat(google).isEqualTo(ProviderType.GOOGLE);
    }

    @Test
    @DisplayName("of: 유효하지 않은 값이면 UserException(INVALID_PROVIDER_TYPE)")
    void ofShouldThrowWhenInvalid() {
        // given
        String invalid = "FACEBOOK";

        // when
        UserException ex = catchThrowableOfType(() -> ProviderType.of(invalid), UserException.class);

        // then
        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.INVALID_PROVIDER_TYPE);
    }
}
