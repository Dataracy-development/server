package com.dataracy.modules.comment.application.port.in.command.count;

public interface DecreaseLikeCountUseCase {
/**
 * 지정된 댓글의 좋아요 개수를 1 감소시킵니다.
 *
 * @param commentId 좋아요 개수를 감소시킬 대상 댓글의 식별자
 */
void decreaseLikeCount(Long commentId);
}
