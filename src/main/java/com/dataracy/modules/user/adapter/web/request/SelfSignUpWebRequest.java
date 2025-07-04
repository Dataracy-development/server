package com.dataracy.modules.user.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(description = "자체 회원가입을 위한 요청 DTO")
public record SelfSignUpWebRequest(
        @Schema(description = "이메일", example = "example@gmail.com")
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email,

        @Schema(description = "비밀번호", example = "juuuunny123@", minLength = 8, maxLength = 20)
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력하세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자리여야 합니다."
        )
        String password,

        @Schema(description = "닉네임 (2~8자)", example = "주니", minLength = 2, maxLength = 8)
        @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력하세요.")
        String nickname,

        @Schema(description = "작성자 유형", example = "3")
        @Min(1)
        Long authorLevelId,

        @Schema(description = "직업", example = "3")
        @Min(1)
        Long occupationId,

        @Schema(description = "흥미 도메인", example = "[1, 3]")
        List<Long> topicIds,

        @Schema(description = "방문 경로", example = "3")
        @Min(1)
        Long visitSourceId,

        @Schema(description = "광고 약관 동의 여부", example = "true")
        @NotNull(message = "광고 이용약관 동의 여부를 입력해주세요.")
        Boolean isAdTermsAgreed
) {}
