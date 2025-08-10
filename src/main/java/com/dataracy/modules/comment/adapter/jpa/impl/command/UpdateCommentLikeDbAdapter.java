package com.dataracy.modules.comment.adapter.jpa.impl.command;

import com.dataracy.modules.comment.adapter.jpa.repository.CommentJpaRepository;
import com.dataracy.modules.comment.application.port.out.command.update.UpdateCommentLikePort;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UpdateCommentLikeDbAdapter implements UpdateCommentLikePort {
    private final CommentJpaRepository commentJpaRepository;

    /**
     * 주어진 댓글 ID에 해당하는 댓글의 좋아요 수를 1 증가시킵니다.
     *
     * @param commentId 좋아요 수를 증가시킬 댓글의 ID
     */
    @Override
    public void increaseLikeCount(Long commentId) {
        commentJpaRepository.increaseLikeCount(commentId);
        LoggerFactory.db().logUpdate("CommentEntity", String.valueOf(commentId), "댓글 좋아요가 완료되었습니다.");
    }

    /**
     * 지정된 댓글의 좋아요 수를 1 감소시킵니다.
     *
     * @param commentId 좋아요 수를 감소시킬 댓글의 ID
     */
    @Override
    public void decreaseLikeCount(Long commentId) {
        commentJpaRepository.decreaseLikeCount(commentId);
        LoggerFactory.db().logUpdate("CommentEntity", String.valueOf(commentId), "댓글 좋아요 취소가 완료되었습니다.");
    }
}
