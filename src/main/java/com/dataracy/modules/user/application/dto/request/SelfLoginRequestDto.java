package com.dataracy.modules.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "자체 로그인을 위한 요청 DTO")
public record SelfLoginRequestDto(

        @Schema(description = "이메일", example = "example@gmail.com")
        @NotBlank(message = "이메일은 공백이나 빈칸일 수 없습니다.")
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email,

        @Schema(description = "비밀번호", example = "juuuunny123@", minLength = 8, maxLength = 20)
        @NotBlank(message = "비밀번호는 공백이나 빈칸일 수 없습니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력하세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자리여야 합니다."
        )
        String password
) {}
