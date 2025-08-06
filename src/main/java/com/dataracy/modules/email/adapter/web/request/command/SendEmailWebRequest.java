package com.dataracy.modules.email.adapter.web.request.command;

import com.dataracy.modules.common.support.annotation.ValidEnumValue;
import com.dataracy.modules.email.domain.enums.EmailVerificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 이메일 인증 코드 전송을 위한 이메일 웹 요청
 * @param email
 */
@Schema(description = "이메일 인증 코드를 보내는 웹 요청 DTO")
public record SendEmailWebRequest(
        @Schema(description = "이메일", example = "example@gmail.com")
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "이메일을 형식에 맞게 입력해주세요")
        String email,

        @Schema(description = "이메일 전송 목적", allowableValues = {"SIGN_UP", "PASSWORD_SEARCH", "PASSWORD_RESET"})
        @ValidEnumValue(
                enumClass = EmailVerificationType.class,
                required = true,
                message = "이메일 전송 목적은 SIGN_UP, PASSWORD_SEARCH, PASSWORD_RESET만 가능합니다."
        )
        String purpose
) {}
