package com.dataracy.modules.auth.application.service.token;

import com.dataracy.modules.auth.application.port.out.token.BlackListTokenPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlackListTokenServiceTest {

    @Mock
    private BlackListTokenPort blackListTokenPort;

    private BlackListTokenService blackListTokenService;

    @BeforeEach
    void setUp() {
        blackListTokenService = new BlackListTokenService(blackListTokenPort);
    }

    @Nested
    @DisplayName("addToBlackList 메서드 테스트")
    class AddToBlackListTest {

        @Test
        @DisplayName("성공: 토큰을 블랙리스트에 추가")
        void addToBlackList_정상토큰_블랙리스트추가() {
            // given
            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
            long expirationMillis = 3600000L; // 1시간

            // when
            blackListTokenService.addToBlackList(token, expirationMillis);

            // then
            then(blackListTokenPort).should().setBlackListToken(token, expirationMillis);
        }

        @Test
        @DisplayName("성공: 긴 토큰도 정상 처리")
        void addToBlackList_긴토큰_정상처리() {
            // given
            String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
            long expirationMillis = 7200000L; // 2시간

            // when
            blackListTokenService.addToBlackList(longToken, expirationMillis);

            // then
            then(blackListTokenPort).should().setBlackListToken(longToken, expirationMillis);
        }

        @Test
        @DisplayName("성공: 빈 토큰도 처리")
        void addToBlackList_빈토큰_처리() {
            // given
            String emptyToken = "";
            long expirationMillis = 1800000L; // 30분

            // when
            blackListTokenService.addToBlackList(emptyToken, expirationMillis);

            // then
            then(blackListTokenPort).should().setBlackListToken(emptyToken, expirationMillis);
        }
    }

    @Nested
    @DisplayName("isBlacklisted 메서드 테스트")
    class IsBlacklistedTest {

        @Test
        @DisplayName("성공: 블랙리스트에 있는 토큰 확인")
        void isBlacklisted_블랙리스트토큰_true반환() {
            // given
            String token = "blacklisted-token";

            // when
            blackListTokenService.isBlacklisted(token);

            // then
            then(blackListTokenPort).should().isBlacklisted(token);
        }

        @Test
        @DisplayName("성공: 일반 토큰 확인")
        void isBlacklisted_일반토큰_확인() {
            // given
            String token = "normal-token";

            // when
            blackListTokenService.isBlacklisted(token);

            // then
            then(blackListTokenPort).should().isBlacklisted(token);
        }
    }
}