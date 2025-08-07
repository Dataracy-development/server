package com.dataracy.modules.comment.application.service.command;

import com.dataracy.modules.comment.application.port.in.command.count.DecreaseLikeCountUseCase;
import com.dataracy.modules.comment.application.port.in.command.count.IncreaseLikeCountUseCase;
import com.dataracy.modules.comment.application.port.out.command.update.UpdateCommentLikePort;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UpdateCommentLikeCountService implements
        IncreaseLikeCountUseCase,
        DecreaseLikeCountUseCase
{
    private final UpdateCommentLikePort updateCommentLikePort;

    /**
     * 지정된 댓글의 좋아요 개수를 1 감소시킵니다.
     *
     * @param commentId 좋아요 개수를 감소시킬 대상 댓글의 ID
     */
    @Override
    @Transactional
    public void decreaseLikeCount(Long commentId) {
        Instant startTime = LoggerFactory.service().logStart("DecreaseLikeCountUseCase", "댓글 좋아요 취소 서비스 시작 commentId=" + commentId);
        updateCommentLikePort.decreaseLikeCount(commentId);
        LoggerFactory.service().logSuccess("DecreaseLikeCountUseCase", "댓글 좋아요 취소 서비스 종료 commentId=" + commentId, startTime);
    }

    /**
     * 지정된 댓글의 좋아요 수를 1 증가시킵니다.
     *
     * @param commentId 좋아요 수를 증가시킬 댓글의 ID
     */
    @Override
    @Transactional
    public void increaseLikeCount(Long commentId) {
        Instant startTime = LoggerFactory.service().logStart("IncreaseLikeCountUseCase", "댓글 좋아요 서비스 시작 commentId=" + commentId);
        updateCommentLikePort.increaseLikeCount(commentId);
        LoggerFactory.service().logSuccess("IncreaseLikeCountUseCase", "댓글 좋아요 서비스 종료 commentId=" + commentId, startTime);
    }
}
