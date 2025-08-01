package com.dataracy.modules.user.application.service.command.password;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.port.in.password.ChangePasswordUseCase;
import com.dataracy.modules.user.application.port.out.jpa.UserCommandPort;
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
public class UserPasswordCommandService implements ChangePasswordUseCase {
    private final PasswordEncoder passwordEncoder;

    private final UserQueryPort userQueryPort;
    private final UserCommandPort userCommandPort;

    private static final String USE_CASE = "ChangePasswordUseCase";

    /**
     * 주어진 유저의 비밀번호를 새 비밀번호로 변경한다.
     *
     * Google 또는 Kakao 계정으로 가입한 유저는 비밀번호 변경이 불가하며, 존재하지 않는 유저 ID일 경우 예외가 발생한다.
     *
     * @param userId 비밀번호를 변경할 유저의 ID
     * @param requestDto 새 비밀번호와 비밀번호 확인값이 포함된 요청 DTO
     * @throws UserException 존재하지 않는 유저이거나 비밀번호 변경이 허용되지 않는 경우 발생
     */
    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart(USE_CASE, "비밀번호 변경 서비스 시작 userId=" + userId);
        // 비밀번호 - 비밀번호 확인 검증
        requestDto.validatePasswordMatch();

        // 해당 유저가 비밀번호를 변경할 수 있는 상태인지 확인한다.
        User savedUser = userQueryPort.findUserById(userId)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(USE_CASE, "[비밀번호 변경] 사용자를 찾을 수 없습니다. userId=" + userId);
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
        savedUser.validatePasswordChangable();

        // 패스워드 암호화 및 변경
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        userCommandPort.changePassword(userId, encodedPassword);

        LoggerFactory.service().logSuccess(USE_CASE, "비밀번호 변경 서비스 성공 userId=" + userId, startTime);
    }
}
