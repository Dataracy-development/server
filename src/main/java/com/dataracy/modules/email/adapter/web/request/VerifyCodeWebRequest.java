package com.dataracy.modules.email.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 이메일 인증 코드 검증을 위한 웹 요청 DTO
 * @param email 이메일
 * @param code 인증코드
 */
public record VerifyCodeWebRequest(
        @Schema(description = "이메일", example = "example@gmail.com")
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "이메일을 형식에 맞게 입력해주세요")
        String email,

        @Schema(description = "인증코드", example = "123456")
        @NotBlank(message = "인증번호를 입력해주세요")
        @Pattern(regexp = "^\\d{6}$", message = "인증번호 6자리를 입력해주세요")
        String code
) {}
