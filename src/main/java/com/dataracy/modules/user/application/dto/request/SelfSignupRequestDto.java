package com.dataracy.modules.user.application.dto.request;

import com.dataracy.modules.common.annotation.ValidEnumValue;
import com.dataracy.modules.user.domain.enums.AuthorLevelStatusType;
import com.dataracy.modules.user.domain.enums.InterestDomainStatusType;
import com.dataracy.modules.user.domain.enums.OccupationStatusType;
import com.dataracy.modules.user.domain.enums.VisitSourceStatusType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(description = "자체 회원가입을 위한 요청 DTO")
public record SelfSignupRequestDto(

        @Schema(description = "이메일", example = "example@gmail.com")
        @NotBlank(message = "이메일은 공백이나 빈칸일 수 없습니다.")
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email,

        @Schema(description = "비밀번호", example = "juuuunny123@", minLength = 8, maxLength = 20)
        @NotBlank(message = "비밀번호는 공백이나 빈칸일 수 없습니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력하세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자리여야 합니다."
        )
        String password,

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
        List<String> topics,

        @Schema(description = "방문 경로", allowableValues = {"SNS", "검색엔진", "지인추천", "커뮤니티", "블로그", "광고", "기타"})
        @ValidEnumValue(enumClass = VisitSourceStatusType.class, required = false, message = "방문 경로는 SNS, 검색엔진, 지인추천, 커뮤니티, 블로그, 광고, 기타만 가능하다.")
        String visitSource,

        @Schema(description = "광고 약관 동의 여부", example = "true")
        @NotNull(message = "광고 이용약관 동의 여부를 입력해주세요.")
        Boolean isAdTermsAgreed

) {}
