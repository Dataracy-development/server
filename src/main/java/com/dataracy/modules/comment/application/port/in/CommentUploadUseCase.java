package com.dataracy.modules.comment.application.port.in;

import com.dataracy.modules.comment.application.dto.request.CommentUploadRequest;

public interface CommentUploadUseCase {
    /****
 * 지정된 프로젝트와 사용자에 대해 댓글을 업로드합니다.
 *
 * @param projectId 댓글이 업로드될 프로젝트의 식별자
 * @param userId 댓글을 작성하는 사용자의 식별자
 * @param requestDto 업로드할 댓글의 내용 및 정보를 담은 요청 객체
 */
void upload(Long projectId, Long userId, CommentUploadRequest requestDto);
}
