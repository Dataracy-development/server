package com.dataracy.modules.comment.application.port.in;

public interface IncreaseLikeCountUseCase {
/**
 * 지정된 댓글의 좋아요 수를 1 증가시킵니다.
 *
 * @param commentId 좋아요 수를 증가시킬 댓글의 식별자
 */
void increaseLike(Long commentId);
}
