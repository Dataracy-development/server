package com.dataracy.modules.email.presentation;

import com.dataracy.modules.common.dto.SuccessResponse;
import com.dataracy.modules.email.application.EmailApplicationService;
import com.dataracy.modules.email.application.dto.request.SendEmailRequestDto;
import com.dataracy.modules.email.application.dto.request.VerifyCodeRequestDto;
import com.dataracy.modules.email.status.EmailSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class EmailController {

    private final EmailApplicationService emailApplicationService;

    @PostMapping("/email/send")
    public ResponseEntity<SuccessResponse<Void>> send(
            @RequestBody SendEmailRequestDto requestDto
    ) {
        emailApplicationService.sendVerificationEmail(requestDto.email());
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(EmailSuccessStatus.OK_SEND_EMAIL_CODE));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<SuccessResponse<Void>> verify(@RequestBody VerifyCodeRequestDto requestDto) {
        emailApplicationService.verifyCode(requestDto.email(), requestDto.code());
        return ResponseEntity.status(HttpStatus.OK).body(SuccessResponse.of(EmailSuccessStatus.OK_VERIFY_EMAIL_CODE));
    }
}
