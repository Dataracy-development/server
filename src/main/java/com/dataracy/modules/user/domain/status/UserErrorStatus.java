package com.dataracy.modules.user.domain.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import com.dataracy.modules.user.domain.enums.ProviderType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorStatus implements BaseErrorCode {
    // 회원 상태 오류
    ALREADY_SIGN_UP_USER(HttpStatus.CONFLICT, "USER-001", "이미 가입된 계정입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "USER-002", "해당 유저가 존재하지 않습니다."),

    NOT_SAME_PASSWORD(HttpStatus.BAD_REQUEST, "USER-003", "비밀번호와 비밀번호 확인은 동일해야합니다"),


    // 닉네임, 이메일, 패스워드 중복
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "USER-003", "이미 사용 중인 닉네임입니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "USER-004", "중복된 이메일은 사용할 수 없습니다"),
    DUPLICATED_PASSWORD(HttpStatus.BAD_REQUEST, "USER-005", "이전과 동일한 비밀번호입니다."),
    DUPLICATED_GOOGLE_EMAIL(HttpStatus.CONFLICT, "USER-004", "동일한 이메일을 사용한 구글 계정이 있습니다. 구글 로그인을 진행해주세요"),
    DUPLICATED_KAKAO_EMAIL(HttpStatus.CONFLICT, "USER-004", "동일한 이메일을 사용한 카카오 계정이 있습니다. 카카오 로그인을 진행해주세요"),
    DUPLICATED_LOCAL_EMAIL(HttpStatus.CONFLICT, "USER-004", "동일한 이메일을 사용한 자체 로그인 계정이 있습니다. 자체 로그인을 진행해주세요"),

    // 레지스터 토큰 만료
    EXPIRED_REGISTER_TOKEN(HttpStatus.FORBIDDEN, "USER-006", "추가 정보 입력 시간이 만료되었습니다. 다시 소셜 로그인을 시도해주세요."),

    // ENUM 타입 관련 잘못된 요청
    INVALID_ROLE_TYPE(HttpStatus.BAD_REQUEST, "USER-007", "유효하지 않은 역할 타입입니다. (ROLE_USER, ROLE_ADMIN, ROLE_ANONYMOUS 중 하나여야 합니다.)"),
    INVALID_PROVIDER_TYPE(HttpStatus.BAD_REQUEST, "USER-008", "유효하지 않은 로그인 제공자입니다. (LOCAL, KAKAO, GOOGLE 중 하나여야 합니다.)"),
    INVALID_AUTHOR_LEVEL_TYPE(HttpStatus.BAD_REQUEST, "USER-009", "유효하지 않은 사용자 유형입니다. (초심자, 실무자, 전문가, GPT활용 중 하나여야 합니다.)"),
    INVALID_OCCUPATION_TYPE(HttpStatus.BAD_REQUEST, "USER-010", "유효하지 않은 직업입니다. (학생, 개발자, 기획자, 디자이너, 마케터, 기타 중 하나여야 합니다.)"),
    INVALID_DOMAIN_TOPIC_TYPE(HttpStatus.BAD_REQUEST, "USER-011", "유효하지 않은 도메인 토픽입니다. (프론트엔드, 백엔드, 인공지능, 데이터분석, 보안, 디자인, 스타트업 중 하나여야 합니다.)"),
    INVALID_VISIT_SOURCE_TYPE(HttpStatus.BAD_REQUEST, "USER-012", "유효하지 않은 방문 경로입니다. (SNS, 검색엔진, 지인추천, 커뮤니티, 블로그, 광고, 기타 중 하나여야 합니다.)")
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
