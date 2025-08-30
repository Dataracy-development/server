package com.dataracy.modules.user.adapter.web.request.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "회원정보 수정을 위한 웹 요청 DTO")
public record ModifyUserInfoWebRequest(
        @Schema(description = "닉네임 (2~8자)", example = "주니", minLength = 2, maxLength = 8)
        @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력하세요.")
        String nickname,

        @Schema(description = "작성자 유형 아이디", example = "3")
        @NotNull(message = "작성자 유형 아이디는 필수값입니다.")
        @Min(1)
        Long authorLevelId,

        @Schema(description = "직업 아이디", example = "3")
        @Min(1)
        Long occupationId,

        @Schema(description = "흥미있는 토픽 아이디 리스트", example = "[1, 3]")
        @NotNull(message = "흥미있는 토픽 리스트에 null은 올 수 없습니다. 리스트 또는 배열 형식으로 입력해주세요.")
        List<Long> topicIds,

        @Schema(description = "방문 경로 아이디", example = "3")
        @Min(1)
        Long visitSourceId,

        @Schema(description = "자기 소개 글", example = "안녕하세요. 저는 ~~입니다.")
        String introductionText
) {}
