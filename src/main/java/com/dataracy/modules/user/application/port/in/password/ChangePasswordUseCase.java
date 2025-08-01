package com.dataracy.modules.user.application.port.in.password;

import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;

public interface ChangePasswordUseCase {
    /**
 * 사용자의 비밀번호를 변경합니다.
 *
 * @param userId 비밀번호를 변경할 사용자의 ID
 * @param requestDto 비밀번호 변경 요청 정보를 담은 객체
 */
    void changePassword(Long userId, ChangePasswordRequest requestDto);
}
