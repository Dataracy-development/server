package com.dataracy.modules.user.application.port.in.user;

/**
 * 이메일 중복 체크 유스케이스
 */
public interface DuplicateEmailUseCase {
    void validateDuplicatedEmail(String email);
}
