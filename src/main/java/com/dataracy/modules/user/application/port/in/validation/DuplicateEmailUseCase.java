package com.dataracy.modules.user.application.port.in.validation;

public interface DuplicateEmailUseCase {
    /**
     * 이메일 중복 체크
     *
     * @param email 이메일
     */
    void validateDuplicatedEmail(String email);
}
