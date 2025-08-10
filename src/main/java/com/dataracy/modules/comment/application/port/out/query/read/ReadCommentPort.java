package com.dataracy.modules.comment.application.port.out.query.read;

import com.dataracy.modules.comment.application.dto.response.support.FindCommentWithReplyCountResponse;
import com.dataracy.modules.comment.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReadCommentPort {
    /**
     * 주어진 ID에 해당하는 댓글을 조회합니다.
     *
     * @param commentId 조회할 댓글의 고유 식별자
     * @return 댓글이 존재하면 해당 Comment 객체를, 없으면 빈 Optional을 반환합니다.
     */
    Optional<Comment> findCommentById(Long commentId);

    /**
     * 지정된 프로젝트의 모든 댓글과 각 댓글의 답글 개수를 페이지 단위로 반환합니다.
     *
     * @param projectId 댓글을 조회할 프로젝트의 식별자
     * @param pageable 페이지네이션 정보
     * @return 각 댓글과 해당 답글 개수를 포함하는 페이지 결과
     */
    Page<FindCommentWithReplyCountResponse> findComments(Long projectId, Pageable pageable);

    /**
     * 특정 프로젝트 내에서 지정된 댓글에 대한 답글 목록을 페이지 단위로 조회합니다.
     *
     * @param projectId   답글이 속한 프로젝트의 ID
     * @param commentId   답글의 대상이 되는 댓글의 ID
     * @param pageable    페이지네이션 정보를 담은 객체
     * @return            답글 댓글(Comment) 객체의 페이지
     */
    Page<Comment> findReplyComments(Long projectId, Long commentId, Pageable pageable);
}
