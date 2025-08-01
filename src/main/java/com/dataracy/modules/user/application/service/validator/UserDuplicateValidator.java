package com.dataracy.modules.user.application.service.validator;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.application.port.out.jpa.UserExistencePort;
import com.dataracy.modules.user.application.port.out.jpa.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDuplicateValidator {
    private final UserExistencePort userExistencePort;
    private final UserQueryPort userQueryPort;

    /**
     * 닉네임 중복 확인
     *
     * @param nickname 닉네임
     */
    public void duplicateNickname(String nickname) {
        if (userExistencePort.existsByNickname(nickname)) {
            LoggerFactory.service().logWarning("User", "닉네임: " + nickname + "은 중복된 값입니다.");
            throw new UserException(UserErrorStatus.DUPLICATED_NICKNAME);
        }
    }

    /**
     * 주어진 이메일로 등록된 사용자가 있는지 확인하여, 중복된 경우 해당 사용자를 반환한다.
     *
     * @param email 중복 여부를 확인할 이메일 주소
     * @return 이메일이 중복된 경우 해당 사용자를 포함하는 Optional, 중복이 없으면 빈 Optional
     */
    public Optional<User> duplicateEmail(String email) {
        return userQueryPort.findUserByEmail(email);
    }
}
