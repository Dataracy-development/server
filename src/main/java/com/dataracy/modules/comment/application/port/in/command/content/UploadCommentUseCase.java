package com.dataracy.modules.comment.application.port.in.command.content;

import com.dataracy.modules.comment.application.dto.request.command.UploadCommentRequest;
import com.dataracy.modules.comment.application.dto.response.command.UploadCommentResponse;

public interface UploadCommentUseCase {
    /**
 * 지정된 프로젝트에 사용자가 댓글을 업로드합니다.
 *
 * 요청된 내용으로 댓글을 생성하고 업로드 결과를 담은 응답 DTO를 반환합니다.
 *
 * @param projectId 댓글을 업로드할 프로젝트의 식별자
 * @param userId 댓글을 작성하는 사용자의 식별자
 * @param requestDto 업로드할 댓글의 내용 및 관련 정보를 포함하는 요청 DTO
 * @return 업로드된 댓글에 대한 결과를 담은 {@code UploadCommentResponse} DTO
 */
    UploadCommentResponse uploadComment(Long projectId, Long userId, UploadCommentRequest requestDto);
}
