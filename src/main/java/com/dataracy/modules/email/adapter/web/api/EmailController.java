package com.dataracy.modules.email.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.email.adapter.web.mapper.EmailWebMapper;
import com.dataracy.modules.email.adapter.web.request.SendEmailWebRequest;
import com.dataracy.modules.email.adapter.web.request.VerifyCodeWebRequest;
import com.dataracy.modules.email.application.dto.request.SendEmailRequest;
import com.dataracy.modules.email.application.dto.request.VerifyCodeRequest;
import com.dataracy.modules.email.application.port.in.EmailSendUseCase;
import com.dataracy.modules.email.application.port.in.EmailVerifyUseCase;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import com.dataracy.modules.email.domain.status.EmailSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController implements EmailApi {

    private final EmailWebMapper emailWebMapper;

    private final EmailSendUseCase emailSendUseCase;
    private final EmailVerifyUseCase emailVerifyUseCase;

    /**
     * 회원가입 이메일 인증 코드 발송
     * @param webRequest 인증 코드 발송 웹 요청 DTO
     * @return 인증 코드 발송 성공
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> send(
            SendEmailWebRequest webRequest
    ) {
        SendEmailRequest requestDto = emailWebMapper.toApplicationDto(webRequest);
        // 전송 목적 확인
        EmailVerificationType verificationType = EmailVerificationType.of(webRequest.purpose());

        // 인증 코드 전송
        emailSendUseCase.sendEmailVerificationCode(requestDto.email(), verificationType);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(EmailSuccessStatus.OK_SEND_EMAIL_CODE));
    }

    /**
     * 이메일 인증 코드 검증
     * @param webRequest 인증 코드 검증 웹 요청 DTO
     * @return 검증 성공
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> verify(VerifyCodeWebRequest webRequest) {
        VerifyCodeRequest requestDto = emailWebMapper.toApplicationDto(webRequest);
        // 전송 목적 확인
        EmailVerificationType verificationType = EmailVerificationType.of(webRequest.purpose());

        // 인증 코드 검증
        emailVerifyUseCase.verifyCode(requestDto.email(), requestDto.code(), verificationType);
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(EmailSuccessStatus.OK_VERIFY_EMAIL_CODE));
    }
}
