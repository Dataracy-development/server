package com.dataracy.modules.comment.application.service.query;

import com.dataracy.modules.comment.application.dto.response.CommentLabelResponse;
import com.dataracy.modules.comment.application.dto.response.CommentWithReplyCountResponse;
import com.dataracy.modules.comment.application.dto.response.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.FindReplyCommentResponse;
import com.dataracy.modules.comment.application.mapper.FindCommentDtoMapper;
import com.dataracy.modules.comment.application.port.in.FindCommentListUseCase;
import com.dataracy.modules.comment.application.port.in.FindReplyCommentListUseCase;
import com.dataracy.modules.comment.application.port.in.FindUserIdByCommentIdUseCase;
import com.dataracy.modules.comment.application.port.out.CommentRepositoryPort;
import com.dataracy.modules.comment.application.port.query.CommentQueryRepositoryPort;
import com.dataracy.modules.comment.domain.exception.CommentException;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.comment.domain.status.CommentErrorStatus;
import com.dataracy.modules.reference.application.port.in.authorlevel.GetAuthorLevelLabelFromIdUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUserAuthorLevelIdsUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUserThumbnailUseCase;
import com.dataracy.modules.user.application.port.in.user.FindUsernameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentQueryService implements
        FindCommentListUseCase,
        FindUserIdByCommentIdUseCase,
        FindReplyCommentListUseCase
{

    private final FindCommentDtoMapper findCommentDtoMapper;

    private final CommentQueryRepositoryPort commentQueryRepositoryPort;
    private final CommentRepositoryPort commentRepositoryPort;

    private final FindUsernameUseCase findUsernameUseCase;
    private final FindUserThumbnailUseCase findUserThumbnailUseCase;
    private final FindUserAuthorLevelIdsUseCase findUserAuthorLevelIdsUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;

    @Override
    public Long findUserIdByCommentId(Long commentId) {
        return commentRepositoryPort.findUserIdByCommentId(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorStatus.NOT_FOUND_COMMENT));
    }

    @Override
    public Page<FindCommentResponse> findComments(Long projectId, Pageable pageable) {
        Page<CommentWithReplyCountResponse> savedComments = commentQueryRepositoryPort.findComments(projectId, pageable);

        List<Long> userIds = savedComments.stream()
                .map(dto -> dto.comment().getUserId())
                .toList();

        CommentLabelResponse result = getCommentLabelResponse(userIds);

        return savedComments.map(wrapper -> {
            Comment comment = wrapper.comment();
            Long authorLevelId = Long.parseLong(result.userAuthorLevelIds().get(comment.getUserId()));
            return findCommentDtoMapper.toResponseDto(
                    comment,
                    result.usernameMap().get(comment.getUserId()),
                    result.userThumbnailMap().get(comment.getUserId()),
                    result.userAuthorLevelLabelMap().get(authorLevelId),
                    wrapper.replyCount()
            );
        });
    }

    @Override
    public Page<FindReplyCommentResponse> findReplyComments(Long projectId, Long commentId, Pageable pageable) {
        Page<Comment> savedComments = commentQueryRepositoryPort.findReplyComments(projectId, commentId, pageable);

        List<Long> userIds = savedComments.stream()
                .map(Comment::getUserId)
                .toList();

        CommentLabelResponse result = getCommentLabelResponse(userIds);

        return savedComments.map(comment -> {
            Long authorLevelId = Long.parseLong(result.userAuthorLevelIds().get(comment.getUserId()));
            return findCommentDtoMapper.toResponseDto(
                    comment,
                    result.usernameMap().get(comment.getUserId()),
                    result.userThumbnailMap().get(comment.getUserId()),
                    result.userAuthorLevelLabelMap().get(authorLevelId)
            );
        });
    }

    private CommentLabelResponse getCommentLabelResponse(List<Long> userIds) {
        Map<Long, String> usernameMap = findUsernameUseCase.findUsernamesByIds(userIds);
        Map<Long, String> userThumbnailMap = findUserThumbnailUseCase.findUserThumbnailsByIds(userIds);
        Map<Long, String> userAuthorLevelIds = findUserAuthorLevelIdsUseCase.findUserAuthorLevelIds(userIds);

        List<Long> authorLevelIds = userAuthorLevelIds.values().stream()
                .map(Long::parseLong)
                .distinct()
                .toList();

        Map<Long, String> userAuthorLevelLabelMap = getAuthorLevelLabelFromIdUseCase.getLabelsByIds(authorLevelIds);
        CommentLabelResponse result = new CommentLabelResponse(usernameMap, userThumbnailMap, userAuthorLevelIds, userAuthorLevelLabelMap);
        return result;
    }
}
