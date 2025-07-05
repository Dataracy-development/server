package com.dataracy.modules.auth.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "자체 로그인을 위한 Web 요청")
public record SelfLoginWebRequest(
        @Schema(description = "이메일", example = "example@gmail.com")
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "이메일을 형식에 맞게 입력해주세요")
        String email,

        @Schema(description = "비밀번호", example = "juuuunny123@", minLength = 8)
        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 8, message = "비밀번호는 8자 이상 20자 이하로 입력하세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
                message = "비밀번호는 영문, 숫자, 특수문자가 포함된 8자리 이상 문자열입니다"
        )
        String password
) {}
