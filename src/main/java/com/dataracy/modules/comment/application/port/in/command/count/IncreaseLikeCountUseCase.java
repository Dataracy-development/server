package com.dataracy.modules.comment.application.port.in.command.count;

public interface IncreaseLikeCountUseCase {
/**
 * 지정된 댓글의 좋아요 개수를 1만큼 증가시킵니다.
 *
 * @param commentId 좋아요 개수를 증가시킬 대상 댓글의 고유 식별자
 */
void increaseLikeCount(Long commentId);
}
