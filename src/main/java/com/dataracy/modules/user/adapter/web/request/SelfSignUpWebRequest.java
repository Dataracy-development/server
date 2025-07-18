package com.dataracy.modules.user.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(description = "자체 회원가입을 위한 요청 DTO")
public record SelfSignUpWebRequest(
        @Schema(description = "이메일", example = "example@gmail.com")
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "이메일을 형식에 맞게 입력해주세요")
        String email,

        @Schema(description = "비밀번호", example = "juuuunny123@", minLength = 8)
        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
                message = "비밀번호는 영문, 숫자, 특수문자가 포함된 8자리 이상 문자열입니다"
        )
        String password,

        @Schema(description = "비밀번호 확인", example = "juuuunny123@")
        @NotBlank(message = "비밀번호 확인을 입력해주세요")
        String passwordConfirm,

        @Schema(description = "닉네임 (2~8자)", example = "주니", minLength = 2, maxLength = 8)
        @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력하세요")
        String nickname,

        @Schema(description = "작성자 유형", example = "3")
        @Min(1)
        Long authorLevelId,

        @Schema(description = "직업", example = "3")
        Long occupationId,

        @Schema(description = "흥미 도메인", example = "[1, 3]")
        @NotNull(message = "흥미있는 도메인 리스트에 null은 올 수 없습니다.")
        List<Long> topicIds,

        @Schema(description = "방문 경로", example = "3")
        Long visitSourceId,

        @Schema(description = "광고 약관 동의 여부", example = "true")
        @NotNull(message = "광고 이용약관 동의 여부를 입력해주세요.")
        Boolean isAdTermsAgreed
) {}
