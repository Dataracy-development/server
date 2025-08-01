package com.dataracy.modules.user.application.port.in.password;

import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;

public interface ConfirmPasswordUseCase {
    /**
     * 비밀번호 확인
     *
     * @param userId 유저 아이디
     * @param requestDto 비밀번호 확인 도메인 요청 DTO
     */
    void confirmPassword(Long userId, ConfirmPasswordRequest requestDto);
}
