package com.dataracy.modules.user.application.service.validator;

import com.dataracy.modules.user.application.port.out.UserRepositoryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 유효성, 중복 확인
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDuplicateValidator {
    private final UserRepositoryPort userRepositoryPort;

    /**
     * 닉네임 중복 확인
     * @param nickname 닉네임
     */
    public void duplicateNickname(String nickname) {
        if (userRepositoryPort.existsByNickname(nickname)) {
            throw new UserException(UserErrorStatus.DUPLICATED_NICKNAME);
        }
    }

    /**
     * 이메일 중복 확인
     * @param email 이메일
     */
    public void duplicateEmail(String email) {
        if (userRepositoryPort.existsByEmail(email)) {
            throw new UserException(UserErrorStatus.DUPLICATED_EMAIL);
        }
    }
}
