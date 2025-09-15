package com.dataracy.modules.user.application.port.in.query.password;

import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;

public interface ConfirmPasswordUseCase {
    /**
     * 지정된 사용자의 비밀번호를 확인합니다.
     *
     * @param userId 비밀번호를 확인할 사용자의 ID
     * @param requestDto 비밀번호 확인 요청 정보를 담은 DTO
     */
    void confirmPassword(Long userId, ConfirmPasswordRequest requestDto);
}
