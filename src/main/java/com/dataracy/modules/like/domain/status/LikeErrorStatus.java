package com.dataracy.modules.like.domain.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LikeErrorStatus implements BaseErrorCode {

    NOT_FOUND_TARGET_LIKE(HttpStatus.INTERNAL_SERVER_ERROR, "LIKE-001", "해당 좋아요 리소스를 찾을 수 없습니다."),
    FAIL_LIKE_PROJECT(HttpStatus.INTERNAL_SERVER_ERROR, "LIKE-001", "프로젝트에 대한 좋아요 처리에 실패햇습니다."),
    FAIL_UNLIKE_PROJECT(HttpStatus.INTERNAL_SERVER_ERROR, "LIKE-002", "프로젝트에 대한 좋아요 취소 처리에 실패했습니다."),
    FAIL_LIKE_COMMENT(HttpStatus.INTERNAL_SERVER_ERROR, "LIKE-003", "댓글에 대한 좋아요 처리에 실패했습니다."),
    FAIL_UNLIKE_COMMENT(HttpStatus.INTERNAL_SERVER_ERROR, "LIKE-004", "댓글에 대한 좋아요 취소 처리에 실패했습니다."),
    NOT_MATCH_CREATOR(HttpStatus.FORBIDDEN, "LIKE-005", "작성자만 좋아요 및 취소가 가능합니다."),
    INVALID_TARGET_TYPE(HttpStatus.BAD_REQUEST, "LIKE-005", "잘못된 좋아요 타겟 유형입니다."),
    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
