package com.dataracy.modules.user.application.port.in.user;

import com.dataracy.modules.user.application.dto.request.ChangePasswordRequest;

public interface ChangePasswordUseCase {
    void changePassword(Long userId, ChangePasswordRequest requestDto);
}
