package com.dataracy.modules.email.adapter.web.api.command;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.email.adapter.web.mapper.command.EmailCommandWebMapper;
import com.dataracy.modules.email.adapter.web.request.command.SendEmailWebRequest;
import com.dataracy.modules.email.application.dto.request.command.SendEmailRequest;
import com.dataracy.modules.email.application.port.in.command.SendEmailUseCase;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.status.EmailSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class EmailCommandController implements EmailCommandApi {
    private final EmailCommandWebMapper emailCommandWebMapper;

    private final SendEmailUseCase sendEmailUseCase;

    /**
     * 회원가입 이메일 인증 코드 발송
     * @param webRequest 인증 코드 발송 웹 요청 DTO
     * @return 인증 코드 발송 성공
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> sendCode(
            SendEmailWebRequest webRequest
    ) {
        Instant startTime = LoggerFactory.api().logRequest("[SendCode] 이메일 인증 코드 발송 API 요청 시작");
        EmailVerificationType verificationType;

        try {
            SendEmailRequest requestDto = emailCommandWebMapper.toApplicationDto(webRequest);
            // 전송 목적 확인
            verificationType = EmailVerificationType.of(webRequest.purpose());

            // 인증 코드 전송
            sendEmailUseCase.sendEmailVerificationCode(requestDto.email(), verificationType);
        } finally {
            LoggerFactory.api().logResponse("[SendCode] 이메일 인증 코드 발송 API 응답 완료", startTime);
        }

        // 이메일 전송 목적에 따라 응답 형태를 설정하여 반환한다.
        return switch (verificationType) {
            case SIGN_UP -> ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(EmailSuccessStatus.OK_SEND_EMAIL_CODE_SIGN_UP));
            case PASSWORD_SEARCH -> ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(EmailSuccessStatus.OK_SEND_EMAIL_CODE_PASSWORD_SEARCH));
            case PASSWORD_RESET -> ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(EmailSuccessStatus.OK_SEND_EMAIL_CODE_PASSWORD_RESET));
        };
    }
}
