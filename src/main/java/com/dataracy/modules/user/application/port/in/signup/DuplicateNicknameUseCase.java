package com.dataracy.modules.user.application.port.in.signup;

/**
 * 닉네임 중복 체크 유스케이스
 */
public interface DuplicateNicknameUseCase {
    void validateDuplicatedNickname(String nickname);
}
