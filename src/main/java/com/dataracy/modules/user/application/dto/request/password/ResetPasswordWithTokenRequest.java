package com.dataracy.modules.user.application.dto.request.password;

import com.dataracy.modules.user.application.dto.request.PasswordConfirmable;

/**
 * 비밀번호를 재설정하는 애플리케이션 요청 DTO
 *
 * @param resetPasswordToken 비밀번호 재설정을 위한 토큰
 * @param password 비밀번호
 * @param passwordConfirm 비밀번호 확인
 */
public record ResetPasswordWithTokenRequest(
    String resetPasswordToken, String password, String passwordConfirm)
    implements PasswordConfirmable {}
