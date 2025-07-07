package com.dataracy.modules.user.application.port.in.user;

import com.dataracy.modules.user.application.dto.request.ConfirmPasswordRequest;

/**
 * 비밀번호 확인 유스케이스
 */
public interface ConfirmPasswordUseCase {
    void confirmPassword(Long userId, ConfirmPasswordRequest requestDto);
}
