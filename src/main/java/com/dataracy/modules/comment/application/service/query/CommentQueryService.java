package com.dataracy.modules.comment.application.service.query;

import com.dataracy.modules.comment.application.dto.response.CommentLabelResponse;
import com.dataracy.modules.comment.application.dto.response.CommentWithReplyCountResponse;
import com.dataracy.modules.comment.application.dto.response.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.FindReplyCommentResponse;
import com.dataracy.modules.comment.application.mapper.FindCommentDtoMapper;
import com.dataracy.modules.comment.application.port.in.FindCommentListUseCase;
import com.dataracy.modules.comment.application.port.in.FindReplyCommentListUseCase;
import com.dataracy.modules.comment.application.port.in.FindUserIdByCommentIdUseCase;
import com.dataracy.modules.comment.application.port.in.ValidateCommentUseCase;
import com.dataracy.modules.comment.application.port.out.CommentRepositoryPort;
import com.dataracy.modules.comment.application.port.query.CommentQueryRepositoryPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.like.application.port.in.query.FindTargetIdsUseCase;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserAuthorLevelIdsUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.query.extractor.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentQueryService implements
        FindCommentListUseCase,
        FindUserIdByCommentIdUseCase,
        FindReplyCommentListUseCase,
        ValidateCommentUseCase
{

    private final FindCommentDtoMapper findCommentDtoMapper;

    private final CommentQueryRepositoryPort commentQueryRepositoryPort;
    private final CommentRepositoryPort commentRepositoryPort;

    private final FindUsernameUseCase findUsernameUseCase;
    private final FindUserThumbnailUseCase findUserThumbnailUseCase;
    private final FindUserAuthorLevelIdsUseCase findUserAuthorLevelIdsUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final FindTargetIdsUseCase findTargetIdsUseCase;

    /**
     * 주어진 댓글 ID에 해당하는 사용자의 ID를 반환합니다.
     *
     * 댓글이 존재하지 않을 경우 {@code CommentException}이 발생합니다.
     *
     * @param commentId 조회할 댓글의 ID
     * @return 댓글 작성자의 사용자 ID
     */
    @Override
    @Transactional(readOnly = true)
    public Long findUserIdByCommentId(Long commentId) {
        return commentRepositoryPort.findUserIdByCommentId(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT));
    }

    /**
     * 지정된 프로젝트의 댓글 목록을 페이지 단위로 조회하고, 각 댓글에 대해 작성자 정보, 답글 수, 사용자의 좋아요 여부를 포함하여 반환합니다.
     *
     * @param userId 댓글 목록을 조회하는 사용자의 ID
     * @param projectId 댓글을 조회할 프로젝트의 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글, 작성자 정보, 답글 수, 좋아요 여부가 포함된 페이지 결과
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FindCommentResponse> findComments(Long userId, Long projectId, Pageable pageable) {
        Page<CommentWithReplyCountResponse> savedComments = commentQueryRepositoryPort.findComments(projectId, pageable);

        List<Long> userIds = savedComments.stream()
                .map(dto -> dto.comment().getUserId())
                .toList();

        List<Long> commentIds = savedComments.stream()
                .map(dto -> dto.comment().getId())
                .toList();

        List<Long> likedIds = findTargetIdsUseCase.findLikedTargetIds(userId, commentIds, TargetType.COMMENT);

        CommentLabelResponse result = getCommentLabelResponse(userIds);

        return savedComments.map(wrapper -> {
            Comment comment = wrapper.comment();
            Long authorLevelId = Long.parseLong(result.userAuthorLevelIds().get(comment.getUserId()));
            return findCommentDtoMapper.toResponseDto(
                    comment,
                    result.usernameMap().get(comment.getUserId()),
                    result.userThumbnailMap().get(comment.getUserId()),
                    result.userAuthorLevelLabelMap().get(authorLevelId),
                    wrapper.replyCount(),
                    likedIds.contains(comment.getId())
            );
        });
    }

    /**
     * 지정된 프로젝트의 원본 댓글에 대한 답글 목록을 페이지 단위로 조회하고, 각 답글에 대해 작성자 정보(닉네임, 썸네일, 작성자 레벨 라벨)와 사용자의 좋아요 여부를 포함하여 반환합니다.
     *
     * @param userId    현재 사용자의 ID
     * @param projectId 답글이 속한 프로젝트의 ID
     * @param commentId 답글이 달린 원본 댓글의 ID
     * @param pageable  페이지네이션 정보
     * @return          답글 목록과 작성자 메타데이터, 좋아요 여부가 포함된 페이지 객체
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FindReplyCommentResponse> findReplyComments(Long userId, Long projectId, Long commentId, Pageable pageable) {
        Page<Comment> savedComments = commentQueryRepositoryPort.findReplyComments(projectId, commentId, pageable);

        List<Long> userIds = savedComments.stream()
                .map(Comment::getUserId)
                .toList();

        List<Long> commentIds = savedComments.stream()
                .map(Comment::getId)
                .toList();

        List<Long> likedIds = findTargetIdsUseCase.findLikedTargetIds(userId, commentIds, TargetType.COMMENT);

        CommentLabelResponse result = getCommentLabelResponse(userIds);

        return savedComments.map(comment -> {
            Long authorLevelId = Long.parseLong(result.userAuthorLevelIds().get(comment.getUserId()));
            return findCommentDtoMapper.toResponseDto(
                    comment,
                    result.usernameMap().get(comment.getUserId()),
                    result.userThumbnailMap().get(comment.getUserId()),
                    result.userAuthorLevelLabelMap().get(authorLevelId),
                    likedIds.contains(comment.getId())
            );
        });
    }

    /**
     * 사용자 ID 목록에 대해 사용자명, 썸네일, 작성자 레벨 ID, 작성자 레벨 라벨 정보를 조회하여 CommentLabelResponse로 반환합니다.
     *
     * @param userIds 정보를 조회할 사용자 ID 목록
     * @return 각 사용자 ID에 대한 사용자명, 썸네일, 작성자 레벨 ID, 작성자 레벨 라벨이 포함된 CommentLabelResponse
     */
    private CommentLabelResponse getCommentLabelResponse(List<Long> userIds) {
        Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(userIds);
        Map<Long, String> userThumbnailMap = findUserThumbnailUseCase.findUserThumbnailsByIds(userIds);
        Map<Long, String> userAuthorLevelIds = findUserAuthorLevelIdsUseCase.findUserAuthorLevelIds(userIds);

        List<Long> authorLevelIds = userAuthorLevelIds.values().stream()
                .map(Long::parseLong)
                .distinct()
                .toList();

        Map<Long, String> userAuthorLevelLabelMap = getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);
        return new CommentLabelResponse(usernameMap, userThumbnailMap, userAuthorLevelIds, userAuthorLevelLabelMap);
    }

    /**
     * 주어진 댓글 ID에 해당하는 댓글이 존재하는지 검증합니다.
     *
     * 댓글이 존재하지 않을 경우 {@code CommentException}이 발생합니다.
     *
     * @param commentId 검증할 댓글의 ID
     * @throws CommentException 댓글이 존재하지 않을 때 발생
     */
    @Override
    @Transactional(readOnly = true)
    public void validateComment(Long commentId) {
        boolean isValidated = commentRepositoryPort.existsByCommentId(commentId);

        if (!isValidated) {
            throw new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT);
        }
    }
}
