package com.dataracy.modules.comment.application.service.query;

import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import com.dataracy.modules.comment.application.dto.response.support.CommentLabelResponse;
import com.dataracy.modules.comment.application.dto.response.support.FindCommentWithReplyCountResponse;
import com.dataracy.modules.comment.application.mapper.read.FindCommentDtoMapper;
import com.dataracy.modules.comment.application.port.in.query.extractor.FindCommentUserInfoUseCase;
import com.dataracy.modules.comment.application.port.in.query.read.FindCommentListUseCase;
import com.dataracy.modules.comment.application.port.in.query.read.FindReplyCommentListUseCase;
import com.dataracy.modules.comment.application.port.out.query.read.ReadCommentPort;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.like.application.port.in.query.FindTargetIdsUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadCommentService implements
        FindCommentListUseCase,
        FindReplyCommentListUseCase
{
    private final FindCommentDtoMapper findCommentDtoMapper;

    private final ReadCommentPort readCommentPort;

    private final FindCommentUserInfoUseCase findCommentUserInfoUseCase;
    private final FindTargetIdsUseCase findTargetIdsUseCase;

    /**
     * 지정된 프로젝트의 댓글을 페이지 단위로 조회하여 댓글 DTO로 반환한다.
     *
     * 반환되는 각 댓글에는 작성자 이름·프로필 URL·저자 레벨 라벨, 답글 수, 그리고 조회 사용자의 좋아요 여부가 포함됩니다.
     *
     * @param userId 댓글을 조회하는 사용자의 ID (조회한 사용자의 좋아요 여부 판단에 사용)
     * @param projectId 댓글을 조회할 프로젝트의 ID
     * @param pageable 페이지 네비게이션 정보
     * @return 댓글 목록을 담은 페이지(Page&lt;FindCommentResponse&gt;)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FindCommentResponse> findComments(Long userId, Long projectId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("FindCommentListUseCase", "프로젝트의 댓글 목록 조회 서비스 시작 projectId=" + projectId);

        Page<FindCommentWithReplyCountResponse> savedComments = readCommentPort.findComments(projectId, pageable);
        List<Long> userIds = savedComments.stream()
                .map(dto -> dto.comment().getUserId())
                .toList();

        List<Long> commentIds = savedComments.stream()
                .map(dto -> dto.comment().getId())
                .toList();

        List<Long> likedIds = findTargetIdsUseCase.findLikedTargetIds(userId, commentIds, TargetType.COMMENT);

        CommentLabelResponse result = findCommentUserInfoUseCase.findCommentUserInfoBatch(userIds);

        Page<FindCommentResponse> findCommentResponses = savedComments.map(wrapper -> {
            Comment comment = wrapper.comment();
            Long authorLevelId = Long.parseLong(result.userAuthorLevelIds().get(comment.getUserId()));
            return findCommentDtoMapper.toResponseDto(
                    comment,
                    result.usernameMap().get(comment.getUserId()),
                    result.userProfileUrlMap().get(comment.getUserId()),
                    result.userAuthorLevelLabelMap().get(authorLevelId),
                    wrapper.replyCount(),
                    likedIds.contains(comment.getId())
            );
        });

        LoggerFactory.service().logSuccess("FindCommentListUseCase", "프로젝트의 댓글 목록 조회 서비스 종료 projectId=" + projectId, startTime);
        return findCommentResponses;
    }

    /**
         * 지정된 프로젝트의 원본 댓글에 대한 답글 목록을 페이지 단위로 조회한다.
         *
         * 각 답글에는 작성자 닉네임, 프로필 이미지 URL, 작성자 레벨 라벨과 현재 사용자의 좋아요 여부가 포함된 DTO로 변환되어 반환된다.
         *
         * @param userId    현재 사용자의 ID (좋아요 여부 판정에 사용)
         * @param projectId 프로젝트 ID
         * @param commentId 부모 댓글 ID
         * @param pageable  페이지네이션 정보
         * @return          답글 목록(작성자 메타데이터 및 현재 사용자의 좋아요 여부 포함)을 담은 Page 객체
         */
    @Override
    @Transactional(readOnly = true)
    public Page<FindReplyCommentResponse> findReplyComments(Long userId, Long projectId, Long commentId, Pageable pageable) {
        Instant startTime = LoggerFactory.service().logStart("FindReplyCommentListUseCase", "프로젝트의 댓글에 대한 답글 목록 조회 서비스 시작 projectId=" + projectId + ", commentId=" + commentId);

        Page<Comment> savedComments = readCommentPort.findReplyComments(projectId, commentId, pageable);

        List<Long> userIds = savedComments.stream()
                .map(Comment::getUserId)
                .toList();

        List<Long> commentIds = savedComments.stream()
                .map(Comment::getId)
                .toList();

        List<Long> likedIds = findTargetIdsUseCase.findLikedTargetIds(userId, commentIds, TargetType.COMMENT);

        CommentLabelResponse result = findCommentUserInfoUseCase.findCommentUserInfoBatch(userIds);

        Page<FindReplyCommentResponse> findReplyCommentResponses = savedComments.map(comment -> {
            Long authorLevelId = Long.parseLong(result.userAuthorLevelIds().get(comment.getUserId()));
            return findCommentDtoMapper.toResponseDto(
                    comment,
                    result.usernameMap().get(comment.getUserId()),
                    result.userProfileUrlMap().get(comment.getUserId()),
                    result.userAuthorLevelLabelMap().get(authorLevelId),
                    likedIds.contains(comment.getId())
            );
        });

        LoggerFactory.service().logSuccess("FindReplyCommentListUseCase", "프로젝트의 댓글에 대한 답글 목록 조회 서비스 종료 projectId=" + projectId + ", commentId=" + commentId, startTime);
        return findReplyCommentResponses;
    }

}
