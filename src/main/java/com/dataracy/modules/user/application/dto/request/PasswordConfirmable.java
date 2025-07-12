package com.dataracy.modules.user.application.dto.request;

import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

public interface PasswordConfirmable {
    String password();
    String passwordConfirm();

    default void validatePasswordMatch() {
        if (!password().equals(passwordConfirm())) {
            throw new UserException(UserErrorStatus.NOT_SAME_PASSWORD);
        }
    }
}
