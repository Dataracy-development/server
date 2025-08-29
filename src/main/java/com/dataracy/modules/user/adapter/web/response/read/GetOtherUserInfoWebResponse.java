package com.dataracy.modules.user.adapter.web.response.read;

import com.dataracy.modules.dataset.application.dto.response.read.UserDataResponse;
import com.dataracy.modules.project.application.dto.response.read.UserProjectResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(description = "타인 프로필 개인정보 조회 웹 응답 DTO")
public record GetOtherUserInfoWebResponse(
        @Schema(description = "유저 아이디", example = "1")
        Long id,

        @Schema(description = "닉네임", example = "주니")
        String nickname,

        @Schema(description = "작성자 유형 라벨", example = "실무자")
        String authorLevelLabel,

        @Schema(description = "직업 라벨", example = "개발자")
        String occupationLabel,

        @Schema(description = "프로필 이미지 URL", example = "https://www.s3.~~~~")
        String profileImageUrl,

        @Schema(description = "유저 소개글", example = "안녕하세요. 저는 주니입니다.")
        String introductionText,

        @Schema(description = "유저가 업로드한 프로젝트 목록")
        Page<UserProjectResponse> projects,

        @Schema(description = "유저가 업로드한 데이터셋 목록")
        Page<UserDataResponse> datasets
) {}
