package com.dataracy.modules.user.application.port.in.password;

import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;

public interface ChangePasswordUseCase {
    /**
     * 비밀번호 변경
     *
     * @param userId 유저 아이디
     * @param requestDto 비밀번호 변경 도메인 요청 DTO
     */
    void changePassword(Long userId, ChangePasswordRequest requestDto);
}
