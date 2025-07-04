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
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email,

        @Schema(description = "인증코드", example = "123456")
        @NotBlank(message = "인증코드는 공백이나 빈칸일 수 없습니다.")
        @Pattern(regexp = "^\\d{6}$", message = "인증코드는 6자리 숫자여야 합니다.")
        String code
) {}
