package com.dataracy.modules.user.adapter.web.response.read;

import com.dataracy.modules.user.domain.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "사용자 개인정보 조회 웹 응답 DTO")
public record GetUserInfoWebResponse(
        @Schema(description = "유저 아이디", example = "1")
        Long id,

        @Schema(description = "유저 역할", example = "ROLE_USER")
        RoleType role,

        @Schema(description = "이메일", example = "example@gmail.com")
        String email,

        @Schema(description = "닉네임", example = "주니")
        String nickname,

        @Schema(description = "작성자 유형 아이디", example = "1")
        Long authorLevelId,

        @Schema(description = "작성자 유형 라벨", example = "실무자")
        String authorLevelLabel,

        @Schema(description = "직업 아이디", example = "1")
        Long occupationId,

        @Schema(description = "직업 라벨", example = "개발자")
        String occupationLabel,

        @Schema(description = "토픽 아이디 리스트")
        List<Long> topicIds,

        @Schema(description = "토픽 라벨 리스트")
        List<String> topicLabels,

        @Schema(description = "방문 경로 아이디", example = "1")
        Long visitSourceId,

        @Schema(description = "방문 경로", example = "SNS")
        String visitSourceLabel,

        @Schema(description = "프로필 이미지 URL", example = "https://www.s3.~~~~")
        String profileImageUrl,

        @Schema(description = "유저 소개글", example = "안녕하세요. 저는 주니입니다.")
        String introductionText
) {}
