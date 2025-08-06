package com.dataracy.modules.comment.application.port.out.command.update;

public interface UpdateCommentLikePort {
    /**
     * 지정된 댓글의 좋아요 수를 1 증가시킵니다.
     *
     * @param commentId 좋아요 수를 증가시킬 댓글의 ID
     */
    void increaseLikeCount(Long commentId);

    /**
     * 지정된 댓글의 좋아요 수를 1 감소시킵니다.
     *
     * @param commentId 좋아요 수를 감소시킬 댓글의 ID
     */
    void decreaseLikeCount(Long commentId);
}
