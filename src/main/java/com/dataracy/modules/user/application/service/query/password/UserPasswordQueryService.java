package com.dataracy.modules.user.application.service.query.password;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;
import com.dataracy.modules.user.application.port.in.password.ConfirmPasswordUseCase;
import com.dataracy.modules.user.application.port.out.jpa.UserQueryPort;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPasswordQueryService implements
        ConfirmPasswordUseCase
{
    private final PasswordEncoder passwordEncoder;

    private final UserQueryPort userQueryPort;

    /**
     * 주어진 사용자 ID와 비밀번호로 사용자의 비밀번호가 일치하는지 검증한다.
     *
     * 사용자가 존재하지 않거나 비밀번호가 일치하지 않을 경우 {@code UserException}이 발생한다.
     *
     * @param userId 비밀번호를 확인할 대상 사용자의 ID
     * @param requestDto 확인할 비밀번호가 포함된 요청 객체
     */
    @Override
    @Transactional(readOnly = true)
    public void confirmPassword(Long userId, ConfirmPasswordRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart("ConfirmPasswordUseCase", "유저의 비밀번호가 일치하는지 확인하는 서비스 시작");

        User user = userQueryPort.findUserById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning("ConfirmPasswordUseCase", "[비밀번호 확인] 유저 아이디에 해당하는 유저가 존재하지 않습니다. userId=" + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });

        boolean isMatched = user.isPasswordMatch(passwordEncoder, requestDto.password());
        if (!isMatched) {
            LoggerFactory.service().logWarning("ConfirmPasswordUseCase", "[비밀번호 확인]제공받은 비밀번호와 실제 비밀번호가 일치하지 않습니다.");
            throw new UserException(UserErrorStatus.FAIL_CONFIRM_PASSWORD);
        }

        LoggerFactory.service().logSuccess("ConfirmPasswordUseCase", "유저의 비밀번호가 일치하는지 확인하는 서비스 성공", startTime);
    }
}
