package com.dataracy.modules.user.application.dto.request.password;

import com.dataracy.modules.user.application.dto.request.PasswordConfirmable;

public record ResetPasswordWithTokenRequest(
        String resetPasswordToken,
        String password,
        String passwordConfirm
) implements PasswordConfirmable {}
