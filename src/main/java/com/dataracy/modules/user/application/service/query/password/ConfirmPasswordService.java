package com.dataracy.modules.user.application.service.query.password;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;
import com.dataracy.modules.user.application.port.in.query.password.ConfirmPasswordUseCase;
import com.dataracy.modules.user.application.port.out.query.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ConfirmPasswordService implements ConfirmPasswordUseCase {
    private final PasswordEncoder passwordEncoder;

    private final UserQueryPort userQueryPort;

    // Use Case 상수 정의
    private static final String CONFIRM_PASSWORD_USE_CASE = "ConfirmPasswordUseCase";
    
    // 메시지 상수 정의
    private static final String USER_NOT_FOUND_MESSAGE = "유저 아이디에 해당하는 유저가 존재하지 않습니다. userId=";

    /**
     * 주어진 사용자 ID와 비밀번호로 해당 사용자의 비밀번호가 일치하는지 확인한다.
     * 사용자가 존재하지 않거나 비밀번호가 일치하지 않을 경우 UserException이 발생한다.
     *
     * @param userId 비밀번호를 확인할 사용자의 ID
     * @param requestDto 확인할 비밀번호가 포함된 요청 객체
     * @throws UserException 사용자가 존재하지 않거나 비밀번호가 일치하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void confirmPassword(Long userId, ConfirmPasswordRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart(CONFIRM_PASSWORD_USE_CASE, "유저의 비밀번호가 일치하는지 확인하는 서비스 시작");

        User user = userQueryPort.findUserById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(CONFIRM_PASSWORD_USE_CASE, "[비밀번호 확인] " + USER_NOT_FOUND_MESSAGE + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });

        boolean isMatched = user.isPasswordMatch(passwordEncoder, requestDto.password());
        if (!isMatched) {
            LoggerFactory.service().logWarning(CONFIRM_PASSWORD_USE_CASE, "[비밀번호 확인]제공받은 비밀번호와 실제 비밀번호가 일치하지 않습니다.");
            throw new UserException(UserErrorStatus.FAIL_CONFIRM_PASSWORD);
        }

        LoggerFactory.service().logSuccess(CONFIRM_PASSWORD_USE_CASE, "유저의 비밀번호가 일치하는지 확인하는 서비스 성공", startTime);
    }
}
