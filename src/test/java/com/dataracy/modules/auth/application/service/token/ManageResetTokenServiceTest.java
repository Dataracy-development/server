/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.auth.application.service.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.auth.application.port.out.token.ManageResetTokenPort;
import com.dataracy.modules.auth.domain.exception.AuthException;
import com.dataracy.modules.auth.domain.status.AuthErrorStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManageResetTokenServiceTest {

  @Mock private ManageResetTokenPort manageResetTokenPort;

  @InjectMocks private ManageResetTokenService service;

  @Nested
  @DisplayName("saveResetToken")
  class SaveResetToken {

    @Test
    @DisplayName("성공 - 포트에 위임됨")
    void success() {
      // given
      String token = "reset-token";

      // when
      service.saveResetToken(token);

      // then
      then(manageResetTokenPort).should().saveResetToken(token);
    }
  }

  @Nested
  @DisplayName("isValidResetToken")
  class IsValidResetToken {

    @Test
    @DisplayName("성공 - 유효한 토큰이면 true 반환")
    void success() {
      // given
      String token = "valid-reset-token";
      given(manageResetTokenPort.isValidResetToken(token)).willReturn(true);

      // when
      boolean result = service.isValidResetToken(token);

      // then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("실패 - 만료되었거나 유효하지 않으면 AuthException 발생")
    void failWhenInvalid() {
      // given
      String token = "expired-reset-token";
      given(manageResetTokenPort.isValidResetToken(token)).willReturn(false);

      // when
      AuthException ex =
          catchThrowableOfType(() -> service.isValidResetToken(token), AuthException.class);

      // then
      assertThat(ex.getErrorCode()).isEqualTo(AuthErrorStatus.EXPIRED_RESET_PASSWORD_TOKEN);
    }
  }
}
