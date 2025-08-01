package com.dataracy.modules.user.application.port.in.validation;

public interface DuplicateNicknameUseCase {
    /**
 * 주어진 닉네임이 이미 사용 중인지 검증합니다.
 *
 * @param nickname 중복 여부를 확인할 닉네임
 */
    void validateDuplicatedNickname(String nickname);
}
