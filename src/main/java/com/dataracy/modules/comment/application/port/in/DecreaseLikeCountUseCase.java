package com.dataracy.modules.comment.application.port.in;

public interface DecreaseLikeCountUseCase {
/**
 * 지정된 댓글의 좋아요 수를 1 감소시킵니다.
 *
 * @param commentId 좋아요 수를 감소시킬 댓글의 ID
 */
void decreaseLike(Long commentId);
}
