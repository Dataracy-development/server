package com.dataracy.modules.comment.domain.status;

import com.dataracy.modules.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorStatus implements BaseErrorCode {
    FAIL_SAVE_COMMENT(HttpStatus.INTERNAL_SERVER_ERROR, "COMMENT-001", "피드백 댓글 업로드에 실패했습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "COMMENT-002", "해당 피드백 댓글 리소스가 존재하지 않습니다."),
    NOT_FOUND_PARENT_COMMENT(HttpStatus.NOT_FOUND, "COMMENT-003", "해당 부모 댓글 리소스가 존재하지 않습니다."),
    NOT_MATCH_CREATOR(HttpStatus.FORBIDDEN, "COMMENT-004", "작성자만 수정 및 삭제, 복원이 가능합니다."),
    FORBIDDEN_REPLY_COMMENT(HttpStatus.FORBIDDEN, "COMMENT-005", "답글에 다시 답글을 작성할 순 없습니다."),
    MISMATCH_PROJECT_COMMENT(HttpStatus.FORBIDDEN, "COMMENT-006", "해당 프로젝트에 작성된 댓글이 아닙니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
