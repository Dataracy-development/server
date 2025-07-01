package com.dataracy.modules.email.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 인증 코드를 보내는 요청 DTO")
public record SendEmailRequestDto(

        @Schema(description = "이메일", example = "example@gmail.com")
        @NotBlank(message = "이메일은 공백이나 빈칸일 수 없습니다.")
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email

) {
}
