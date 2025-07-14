package com.dataracy.modules.user.application.dto.request;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

public interface PasswordConfirmable {
    /**
 * 사용자가 입력한 비밀번호를 반환합니다.
 *
 * @return 입력된 비밀번호
 */
    String password();

    /**
 * 사용자가 입력한 비밀번호 확인 값을 반환합니다.
 *
 * @return 비밀번호 확인 입력값
 */
    String passwordConfirm();

    /**
     * 비밀번호와 비밀번호 확인 값이 일치하는지 검증합니다.
     *
     * 두 값이 다를 경우 {@code UserException}을 발생시킵니다.
     */
    default void validatePasswordMatch() {
        if (!password().equals(passwordConfirm())) {
            throw new UserException(UserErrorStatus.NOT_SAME_PASSWORD);
        }
    }
}
