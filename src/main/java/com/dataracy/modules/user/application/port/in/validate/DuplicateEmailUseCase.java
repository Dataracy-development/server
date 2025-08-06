package com.dataracy.modules.user.application.port.in.validate;

public interface DuplicateEmailUseCase {
    /**
 * 주어진 이메일이 이미 사용 중인지 중복 여부를 검사합니다.
 *
 * @param email 중복 여부를 확인할 이메일 주소
 */
    void validateDuplicatedEmail(String email);
}
