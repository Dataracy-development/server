package com.dataracy.modules.user.adapter.web.request.validate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "닉네임 중복을 확인하는 웹 요청 DTO")
public record DuplicateNicknameWebRequest(
        @Schema(description = "닉네임 (2~8자)", example = "주니", minLength = 2, maxLength = 8)
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력하세요.")
        String nickname
) {}
