package com.dataracy.modules.user.application.port.in.validation;

public interface DuplicateNicknameUseCase {
    /**
     * 닉네임 중복 체크
     *
     * @param nickname 닉네임
     */
    void validateDuplicatedNickname(String nickname);
}
