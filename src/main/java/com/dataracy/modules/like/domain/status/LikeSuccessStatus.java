package com.dataracy.modules.like.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LikeSuccessStatus implements BaseSuccessCode {

    LIKE_PROJECT(HttpStatus.OK, "LIKE-001", "프로젝트에 대한 좋아요 처리에 성공했습니다."),
    UNLIKE_PROJECT(HttpStatus.OK, "LIKE-002", "프로젝트에 대한 좋아요 취소 처리에 성공했습니다."),
    LIKE_COMMENT(HttpStatus.OK, "LIKE-003", "댓글에 대한 좋아요 처리에 성공했습니다."),
    UNLIKE_COMMENT(HttpStatus.OK, "LIKE-004", "댓글에 대한 좋아요 취소 처리에 성공했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
