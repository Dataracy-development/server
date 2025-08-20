package com.dataracy.modules.user.application.service.command.password;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.auth.application.port.in.token.ManageResetTokenUseCase;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ResetPasswordWithTokenRequest;
import com.dataracy.modules.user.application.port.in.command.password.ChangePasswordUseCase;
import com.dataracy.modules.user.application.port.out.command.UserCommandPort;
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
public class ChangePasswordService implements ChangePasswordUseCase {
    private final PasswordEncoder passwordEncoder;

    private final UserQueryPort userQueryPort;
    private final UserCommandPort userCommandPort;

    private final ManageResetTokenUseCase manageResetTokenUseCase;
    private final JwtValidateUseCase jwtValidateUseCase;

    private static final String USE_CASE = "ChangePasswordUseCase";

    /**
     * 주어진 유저의 비밀번호를 새 비밀번호로 변경한다.
     * Google 또는 Kakao 계정으로 가입한 유저는 비밀번호를 변경할 수 없으며, 존재하지 않는 유저 ID가 입력된 경우 예외가 발생한다.
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

    /**
     * 비밀번호 재설정 토큰을 사용하여 사용자의 비밀번호를 재설정합니다.
     *
     * 비밀번호 재설정 토큰의 유효성을 검증하고, 토큰에서 이메일을 추출하여 해당 사용자를 조회합니다.
     * 사용자가 존재하지 않거나 비밀번호 변경이 불가능한 경우 예외가 발생합니다.
     * 새 비밀번호와 비밀번호 확인 값이 일치하는지 검증한 후, 비밀번호를 암호화하여 저장합니다.
     *
     * @param requestDto 비밀번호 재설정 요청 정보가 담긴 객체
     * @throws UserException 사용자를 찾을 수 없거나 비밀번호 변경이 불가능한 경우 발생
     */
    @Override
    @Transactional
    public void resetPassword(ResetPasswordWithTokenRequest requestDto) {
        Instant startTime = LoggerFactory.service().logStart(USE_CASE, "비밀번호 재설정 서비스 시작");

        // 토큰 유효성 검사
        manageResetTokenUseCase.isValidResetToken(requestDto.resetPasswordToken());
        // 비밀번호 - 비밀번호 확인 검증
        requestDto.validatePasswordMatch();

        String email = jwtValidateUseCase.getEmailFromResetToken(requestDto.resetPasswordToken());
        User savedUser = userQueryPort.findUserByEmail(email)
                .orElseThrow(() -> {
                    LoggerFactory.service().logWarning(USE_CASE, "[비밀번호 재설정] 사용자를 찾을 수 없습니다.");
                    return new UserException(UserErrorStatus.NOT_FOUND_USER);
                });
        savedUser.validatePasswordChangable();

        // 패스워드 암호화 및 변경
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        userCommandPort.changePassword(savedUser.getId(), encodedPassword);

        LoggerFactory.service().logSuccess(USE_CASE, "비밀번호 재설정 서비스 성공", startTime);
    }
}
