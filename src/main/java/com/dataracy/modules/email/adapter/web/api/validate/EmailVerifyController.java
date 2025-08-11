package com.dataracy.modules.email.adapter.web.api.validate;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.adapter.web.mapper.validate.ValidateEmailWebMapper;
import com.dataracy.modules.email.adapter.web.request.validate.VerifyCodeWebRequest;
import com.dataracy.modules.email.application.dto.request.validate.VerifyCodeRequest;
import com.dataracy.modules.email.application.dto.response.GetResetTokenResponse;
import com.dataracy.modules.email.application.port.in.validate.VerifyEmailUseCase;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.status.EmailSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class EmailVerifyController implements EmailVerifyApi {
    private final ValidateEmailWebMapper validateEmailWebMapper;

    private final VerifyEmailUseCase verifyEmailUseCase;

    /**
     * 이메일 인증 코드의 유효성을 검증하고, 성공 시 비밀번호 재설정용 검증이라면 재설정 토큰을 함께 반환합니다.
     *
     * @param webRequest 이메일 인증 코드 검증 요청 정보
     * @return 인증 코드 검증 성공 상태와 비밀번호 재설정 토큰이 포함된 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<GetResetTokenResponse>> verifyCode(VerifyCodeWebRequest webRequest) {
        Instant startTime = LoggerFactory.api().logRequest("[VerifyCode] 이메일 인증 코드 검증 API 요청 시작");
        GetResetTokenResponse resetPasswordToken;

        try {
            VerifyCodeRequest requestDto = validateEmailWebMapper.toApplicationDto(webRequest);
            // 전송 목적 확인
            EmailVerificationType verificationType = EmailVerificationType.of(webRequest.purpose());
            // 인증 코드 검증
            resetPasswordToken = verifyEmailUseCase.verifyCode(requestDto.email(), requestDto.code(), verificationType);
        } finally {
            LoggerFactory.api().logResponse("[VerifyCode] 이메일 인증 코드 검증 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(EmailSuccessStatus.OK_VERIFY_EMAIL_CODE, resetPasswordToken));
    }
}
