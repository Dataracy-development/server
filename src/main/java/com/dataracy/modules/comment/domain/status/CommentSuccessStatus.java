package com.dataracy.modules.comment.domain.status;

import com.dataracy.modules.common.status.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentSuccessStatus implements BaseSuccessCode {

    CREATED_COMMENT(HttpStatus.CREATED, "201", "댓글 작성이 완료되었습니다"),
    MODIFY_COMMENT(HttpStatus.OK, "200", "댓글 수정이 완료되었습니다."),
    DELETE_COMMENT(HttpStatus.OK, "200", "댓글 삭제가 완료되었습니다."),
    GET_COMMENTS(HttpStatus.OK, "200", "댓글 목록 조회가 완료되었습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
