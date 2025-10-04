package com.dataracy.modules.auth.application.service.token;

import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.auth.application.port.out.token.BlackListTokenPort;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BlackListTokenServiceTest {

  // Test constants
  private static final Long THIRTY_MINUTES_IN_MILLIS = 1800000L;
  private static final Long ONE_HOUR_IN_MILLIS = 3600000L;
  private static final Long TWO_HOURS_IN_MILLIS = 7200000L;

  @Mock private BlackListTokenPort blackListTokenPort;

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
    void addToBlackListWithNormalTokenSuccess() {
      // given
      String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
      long expirationMillis = ONE_HOUR_IN_MILLIS; // 1시간

      // when
      blackListTokenService.addToBlackList(token, expirationMillis);

      // then
      then(blackListTokenPort).should().setBlackListToken(token, expirationMillis);
    }

    @Test
    @DisplayName("성공: 긴 토큰도 정상 처리")
    void addToBlackListWithLongTokenSuccess() {
      // given
      String longToken =
          "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
      long expirationMillis = TWO_HOURS_IN_MILLIS;

      // when
      blackListTokenService.addToBlackList(longToken, expirationMillis);

      // then
      then(blackListTokenPort).should().setBlackListToken(longToken, expirationMillis);
    }

    @Test
    @DisplayName("성공: 빈 토큰도 처리")
    void addToBlackListWithEmptyTokenSuccess() {
      // given
      String emptyToken = "";
      long expirationMillis = THIRTY_MINUTES_IN_MILLIS; // 30분

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
    void isBlacklistedWithBlacklistedTokenReturnsTrue() {
      // given
      String token = "blacklisted-token";

      // when
      blackListTokenService.isBlacklisted(token);

      // then
      then(blackListTokenPort).should().isBlacklisted(token);
    }

    @Test
    @DisplayName("성공: 일반 토큰 확인")
    void isBlacklistedWithNormalTokenReturnsFalse() {
      // given
      String token = "normal-token";

      // when
      blackListTokenService.isBlacklisted(token);

      // then
      then(blackListTokenPort).should().isBlacklisted(token);
    }
  }
}
