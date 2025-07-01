package com.dataracy.modules.email.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyCodeRequest(

        @Schema(description = "이메일", example = "example@gmail.com")
        @NotBlank(message = "이메일은 공백이나 빈칸일 수 없습니다.")
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email,

        @Schema(description = "인증코드", example = "123456")
        @NotBlank(message = "인증코드는 공백이나 빈칸일 수 없습니다.")
        @Pattern(regexp = "^\\d{6}$", message = "인증코드는 6자리 숫자여야 합니다.")
        String code

) {
}
