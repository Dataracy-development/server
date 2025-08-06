package com.dataracy.modules.user.adapter.web.api.password;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.adapter.web.mapper.password.UserPasswordWebMapper;
import com.dataracy.modules.user.adapter.web.request.password.ChangePasswordWebRequest;
import com.dataracy.modules.user.adapter.web.request.password.ConfirmPasswordWebRequest;
import com.dataracy.modules.user.application.dto.request.password.ChangePasswordRequest;
import com.dataracy.modules.user.application.dto.request.password.ConfirmPasswordRequest;
import com.dataracy.modules.user.application.port.in.command.password.ChangePasswordUseCase;
import com.dataracy.modules.user.application.port.in.query.password.ConfirmPasswordUseCase;
import com.dataracy.modules.user.domain.status.UserSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class UserPasswordController implements UserPasswordApi {
    private final UserPasswordWebMapper userPasswordWebMapper;

    private final ChangePasswordUseCase changePasswordUseCase;
    private final ConfirmPasswordUseCase confirmPasswordUseCase;

    /**
     * 사용자의 비밀번호를 변경하는 API 엔드포인트이다.
     *
     * @param userId 비밀번호를 변경할 대상 사용자의 ID
     * @param webRequest 비밀번호 변경 요청 데이터
     * @return 비밀번호 변경 성공 시 200 OK와 성공 상태가 포함된 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> changePassword(
            Long userId,
            ChangePasswordWebRequest webRequest
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[ChangePassword] 비밀번호 변경 API 요청 시작");

        try {
            ChangePasswordRequest requestDto = userPasswordWebMapper.toApplicationDto(webRequest);
            changePasswordUseCase.changePassword(userId, requestDto);
        } finally {
            LoggerFactory.api().logResponse("[ChangePassword] 비밀번호 변경 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_CHANGE_PASSWORD));
    }

    /**
     * 사용자의 비밀번호를 확인하고 성공 시 200 OK 응답을 반환합니다.
     *
     * @param userId 비밀번호를 확인할 사용자 ID
     * @param webRequest 비밀번호 확인 요청 데이터
     * @return 비밀번호가 올바른 경우 성공 상태의 응답을 반환합니다.
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> confirmPassword(
            Long userId,
            ConfirmPasswordWebRequest webRequest
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[ConfirmPassword] 비밀번호 확인 API 요청 시작");

        try {
            ConfirmPasswordRequest requestDto = userPasswordWebMapper.toApplicationDto(webRequest);
            confirmPasswordUseCase.confirmPassword(userId, requestDto);
        } finally {
            LoggerFactory.api().logResponse("[ConfirmPassword] 비밀번호 확인 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(UserSuccessStatus.OK_CONFIRM_PASSWORD));
    }
}
