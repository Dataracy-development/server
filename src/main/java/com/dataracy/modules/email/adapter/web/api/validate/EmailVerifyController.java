package com.dataracy.modules.email.adapter.web.api.validate;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.adapter.web.mapper.validate.ValidateEmailWebMapper;
import com.dataracy.modules.email.adapter.web.request.validate.VerifyCodeWebRequest;
import com.dataracy.modules.email.application.dto.request.validate.VerifyCodeRequest;
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
     * 이메일 인증 코드 검증
     * @param webRequest 인증 코드 검증 웹 요청 DTO
     * @return 검증 성공
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> verifyCode(VerifyCodeWebRequest webRequest) {
        Instant startTime = LoggerFactory.api().logRequest("[VerifyCode] 이메일 인증 코드 검증 API 요청 시작");
        try {
            VerifyCodeRequest requestDto = validateEmailWebMapper.toApplicationDto(webRequest);
            // 전송 목적 확인
            EmailVerificationType verificationType = EmailVerificationType.of(webRequest.purpose());
            // 인증 코드 검증
            verifyEmailUseCase.verifyCode(requestDto.email(), requestDto.code(), verificationType);
        } finally {
            LoggerFactory.api().logResponse("[VerifyCode] 이메일 인증 코드 검증 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(EmailSuccessStatus.OK_VERIFY_EMAIL_CODE));
    }
}
