package com.dataracy.modules.user.application.port.in.user;

import com.dataracy.modules.user.application.dto.request.ConfirmPasswordRequest;

public interface ConfirmPasswordUseCase {
    void confirmPassword(Long userId, ConfirmPasswordRequest requestDto);
}
