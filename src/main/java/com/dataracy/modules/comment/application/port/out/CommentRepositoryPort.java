package com.dataracy.modules.comment.application.port.out;

import com.dataracy.modules.comment.application.dto.request.CommentModifyRequest;
import com.dataracy.modules.comment.domain.model.Comment;

import java.util.Optional;

public interface CommentRepositoryPort {
    /**
 * 새로운 댓글을 저장합니다.
 *
 * @param comment 저장할 댓글 객체
 */
void upload(Comment comment);
    /**
 * 지정된 프로젝트와 댓글 ID에 해당하는 댓글을 주어진 수정 요청 정보로 변경합니다.
 *
 * @param projectId 수정할 댓글이 속한 프로젝트의 ID
 * @param commentId 수정할 댓글의 ID
 * @param requestDto 댓글 수정에 필요한 정보가 담긴 요청 객체
 */
void modify(Long projectId, Long commentId, CommentModifyRequest requestDto);
    /**
 * 지정된 프로젝트 ID와 댓글 ID에 해당하는 댓글을 삭제합니다.
 *
 * @param projectId 댓글이 속한 프로젝트의 ID
 * @param commentId 삭제할 댓글의 ID
 */
void delete(Long projectId, Long commentId);
    /**
 * 주어진 댓글 ID로 댓글 작성자의 사용자 ID를 조회합니다.
 *
 * @param commentId 조회할 댓글의 ID
 * @return 댓글 작성자의 사용자 ID가 존재하면 해당 값을 포함한 Optional, 존재하지 않으면 빈 Optional
 */
Optional<Long> findUserIdByCommentId(Long commentId);

/**
 * 지정된 댓글 ID에 해당하는 댓글이 존재하는지 여부를 반환합니다.
 *
 * @param commentId 존재 여부를 확인할 댓글의 ID
 * @return 댓글이 존재하면 true, 그렇지 않으면 false
 */
boolean existsByCommentId(Long commentId);

}

