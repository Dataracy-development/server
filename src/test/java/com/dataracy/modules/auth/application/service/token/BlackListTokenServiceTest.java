package com.dataracy.modules.auth.application.service.token;

import com.dataracy.modules.auth.application.port.out.token.BlackListTokenPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BlackListTokenServiceTest {

    @Mock
    private BlackListTokenPort blackListTokenPort;

    @InjectMocks
    private BlackListTokenService service;

    @Nested
    @DisplayName("addToBlackList")
    class AddToBlackList {

        @Test
        @DisplayName("토큰 블랙리스트 등록 성공")
        void success() {
            // given
            String token = "access-token";
            long expiration = 1000L;

            willDoNothing().given(blackListTokenPort).setBlackListToken(token, expiration);

            // when
            service.addToBlackList(token, expiration);

            // then
            then(blackListTokenPort).should().setBlackListToken(token, expiration);
        }
    }

    @Nested
    @DisplayName("isBlacklisted")
    class IsBlacklisted {

        @Test
        @DisplayName("블랙리스트에 존재하면 true 반환")
        void returnTrue() {
            // given
            String token = "black-token";
            given(blackListTokenPort.isBlacklisted(token)).willReturn(true);

            // when
            boolean result = service.isBlacklisted(token);

            // then
            assertThat(result).isTrue();
            then(blackListTokenPort).should().isBlacklisted(token);
        }

        @Test
        @DisplayName("블랙리스트에 존재하지 않으면 false 반환")
        void returnFalse() {
            // given
            String token = "normal-token";
            given(blackListTokenPort.isBlacklisted(token)).willReturn(false);

            // when
            boolean result = service.isBlacklisted(token);

            // then
            assertThat(result).isFalse();
            then(blackListTokenPort).should().isBlacklisted(token);
        }
    }
}
