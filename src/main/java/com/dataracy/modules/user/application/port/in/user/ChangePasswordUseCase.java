package com.dataracy.modules.user.application.port.in.user;

import com.dataracy.modules.user.application.dto.request.ChangePasswordRequest;

/**
 * 비밀번호 변경 유스케이스
 */
public interface ChangePasswordUseCase {
    void changePassword(Long userId, ChangePasswordRequest requestDto);
}
