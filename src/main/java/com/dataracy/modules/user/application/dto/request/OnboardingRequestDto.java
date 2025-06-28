package com.dataracy.modules.user.application.dto.request;

import com.dataracy.modules.common.annotation.ValidEnumValue;
import com.dataracy.modules.user.domain.enums.AuthorLevelStatusType;
import com.dataracy.modules.user.domain.enums.InterestDomainStatusType;
import com.dataracy.modules.user.domain.enums.OccupationStatusType;
import com.dataracy.modules.user.domain.enums.VisitSourceStatusType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "유저 추가정보 입력 온보딩 요청 DTO")
public record OnboardingRequestDto(

        @Schema(description = "닉네임 (2~8자)", example = "주니", minLength = 2, maxLength = 8)
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력하세요.")
        String nickname,

        @Schema(description = "작성자 유형", allowableValues = {"초심자", "실무자", "전문가", "GPT활용"})
        @ValidEnumValue(enumClass = AuthorLevelStatusType.class, required = true, message = "작성자 유형은 초심자, 실무자, 전문가, GPT활용만 가능하다.")
        String authorLevel,

        @Schema(description = "직업", allowableValues = {"학생", "개발자", "기획자", "디자이너", "마케터", "기타"})
        @ValidEnumValue(enumClass = OccupationStatusType.class, required = false, message = "직업은 학생, 개발자, 기획자, 디자이너, 마케너, 기타만 가능하다.")
        String occupation,

        @Schema(description = "흥미 도메인", example = "[\"프론트엔드\", \"디자인\"]",
                allowableValues = {"프론트엔드", "백엔드", "인공지능", "데이터분석", "보안", "디자인", "스타트업"})
        @ValidEnumValue(enumClass = InterestDomainStatusType.class, required = false, message = "흥미 도메인은 프론트엔드, 백엔드, 인공지능, 데이터분석, 보안, 디자인, 스타트업이 가능하다.")
        List<String> domains,

        @Schema(description = "방문 경로", allowableValues = {"SNS", "검색엔진", "지인추천", "커뮤니티", "블로그", "광고", "기타"})
        @ValidEnumValue(enumClass = VisitSourceStatusType.class, required = false, message = "방문 경로는 SNS, 검색엔진, 지인추천, 커뮤니티, 블로그, 광고, 기타만 가능하다.")
        String visitSource,

        @Schema(description = "광고 약관 동의 여부", example = "true")
        @NotNull(message = "광고 이용약관 동의 여부를 입력해주세요.")
        Boolean isAdTermsAgreed
) {
}
