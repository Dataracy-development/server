package com.dataracy.modules.user.adapter.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "유저 추가정보 입력 온보딩 요청 DTO")
public record OnboardingWebRequest(
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
