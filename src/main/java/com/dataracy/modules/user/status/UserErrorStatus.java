package com.dataracy.modules.user.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorStatus implements BaseErrorCode {

    BAD_REQUEST_ROLE_STATUS_TYPE(HttpStatus.BAD_REQUEST, "USER-001", "잘못된 ROLE 타입입니다. (ROLE_USER, ROLE_ADMIN, ROLE_ANONYMOUS만 가능합니다.)"),
    BAD_REQUEST_PROVIDER_STATUS_TYPE(HttpStatus.BAD_REQUEST, "USER-002", "잘못된 PROVIDER 타입입니다. (LOCAL, KAKAO, GOOGLE만 가능합니다.)"),
    BAD_REQUEST_AUTHOR_LEVEL_STATUS_TYPE(HttpStatus.BAD_REQUEST, "USER-003", "잘못된 사용자 유형입니다. (초심자, 실무자, 전문가, GPT활용만 가능합니다.)"),
    BAD_REQUEST_OCCUPATION_STATUS_TYPE(HttpStatus.BAD_REQUEST, "USER-004", "잘못된 직업 유형입니다. (학생, 개발자, 기획자, 디자이너, 마케터, 기타만 가능합니다.)"),
    BAD_REQUEST_DOMAIN_TOPIC_STATUS_TYPE(HttpStatus.BAD_REQUEST, "USER-005", "잘못된 도메인 토픽 유형입니다. (프론트엔드, 백엔드, 인공지능, 데이터분석, 보안, 디자인, 스타트업만 가능합니다.)"),
    BAD_REQUEST_VISIT_SOURCE_STATUS_TYPE(HttpStatus.BAD_REQUEST, "USER-006", "잘못된 방문 경로입니다. (SNS, 검색엔진, 지인추천, 커뮤니티, 블로그, 광고, 기타만 가능합니다.)"),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "USER-007", "해당하는 유저가 존재하지 않습니다."),
    EXPIRED_REGISTER_TOKEN(HttpStatus.FORBIDDEN, "USER-008", "소셜 회원가입 추가정보 입력을 위한 레지스터 토큰이 만료되었습니다. 다시 소셜로그인을 진행해주세요. 추가정보 입력시간은 10분입니다."),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "USER-09", "중복된 닉네임입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
