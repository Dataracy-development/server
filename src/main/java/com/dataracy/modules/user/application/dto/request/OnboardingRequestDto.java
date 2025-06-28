package com.dataracy.modules.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OnboardingRequestDto(

        @NotBlank(message = "닉네임은 2자 이상 8자 이하로 입력하세요.")
        @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력하세요.")
        String nickname,

        @NotBlank(message = "작성자 유형은 초심자, 실무자, 전문가, GPT활용 중 하나로 입력해주세요.")
        String authorLevel,

        @NotBlank(message = "직업은 학생, 개발자, 기획자, 디자이너, 마케터, 기타 중 하나로 입력해주세요.")
        String occupation,

        @NotBlank(message = "흥미 도메인은 프론트엔드, 백엔드, 인공지능, 데이터분석, 보안, 디자인, 스타트업 중에서 선택해 배열로 넘겨주세요.")
        String[] domains,

        @NotBlank(message = "방문 경로는 SNS, 검색엔진, 지인추천, 커뮤니티, 블로그, 광고, 기타 중 하나로 입력해주세요.")
        String visitSource,

        @NotNull(message = "광고 이용약관 동의 여부를 입력해주세요. (true or false)")
        Boolean isAdTermsAgreed
) {
}
