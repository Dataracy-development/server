package com.dataracy.modules.auth.application.service.token;

import com.dataracy.modules.auth.application.port.out.token.ManageRefreshTokenPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManageRefreshTokenServiceTest {

    @Mock
    private ManageRefreshTokenPort manageRefreshTokenPort;

    @InjectMocks
    private ManageRefreshTokenService service;

    @Nested
    @DisplayName("saveRefreshToken")
    class SaveRefreshToken {

        @Test
        @DisplayName("성공 - 포트에 위임됨")
        void success() {
            // given
            String userId = "1";
            String token = "refresh-token";

            // when
            service.saveRefreshToken(userId, token);

            // then
            then(manageRefreshTokenPort).should().saveRefreshToken(userId, token);
        }
    }

    @Nested
    @DisplayName("getRefreshToken")
    class GetRefreshToken {

        @Test
        @DisplayName("성공 - 저장된 토큰 반환")
        void success() {
            // given
            String userId = "1";
            given(manageRefreshTokenPort.getRefreshToken(userId)).willReturn("saved-token");

            // when
            String token = service.getRefreshToken(userId);

            // then
            assertThat(token).isEqualTo("saved-token");
        }

        @Test
        @DisplayName("실패 - 토큰이 없으면 AuthException 발생")
        void failWhenNull() {
            // given
            String userId = "1";
            given(manageRefreshTokenPort.getRefreshToken(userId)).willReturn(null);

            // when
            AuthException ex = catchThrowableOfType(
                    () -> service.getRefreshToken(userId),
                    AuthException.class
            );

            // then
            assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.EXPIRED_REFRESH_TOKEN);
        }
    }

    @Nested
    @DisplayName("deleteRefreshToken")
    class DeleteRefreshToken {

        @Test
        @DisplayName("성공 - 포트에 위임됨")
        void success() {
            // given
            String userId = "1";

            // when
            service.deleteRefreshToken(userId);

            // then
            then(manageRefreshTokenPort).should().deleteRefreshToken(userId);
        }
    }
}
