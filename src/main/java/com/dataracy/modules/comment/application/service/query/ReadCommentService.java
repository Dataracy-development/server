package com.dataracy.modules.comment.application.service.query;

import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import com.dataracy.modules.comment.application.dto.response.support.CommentLabelResponse;
import com.dataracy.modules.comment.application.dto.response.support.FindCommentWithReplyCountResponse;
import com.dataracy.modules.comment.application.mapper.read.FindCommentDtoMapper;
import com.dataracy.modules.comment.application.port.in.query.read.FindCommentListUseCase;
import com.dataracy.modules.comment.application.port.in.query.read.FindReplyCommentListUseCase;
import com.dataracy.modules.comment.application.port.out.query.read.ReadCommentPort;
import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.common.logging.support.LoggerFactory;
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

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReadCommentService implements
        FindCommentListUseCase,
        FindReplyCommentListUseCase
{
    private final FindCommentDtoMapper findCommentDtoMapper;

    private final ReadCommentPort readCommentPort;

    private final FindUsernameUseCase findUsernameUseCase;
    private final FindUserThumbnailUseCase findUserThumbnailUseCase;
    private final FindUserAuthorLevelIdsUseCase findUserAuthorLevelIdsUseCase;
    private final GetAuthorLevelLabelFromIdUseCase getAuthorLevelLabelFromIdUseCase;
    private final FindTargetIdsUseCase findTargetIdsUseCase;

    /**
     * 지정된 프로젝트의 댓글 목록을 페이지 단위로 조회한다.
     * 각 댓글에 작성자 정보, 답글 수, 사용자의 좋아요 여부를 포함하여 반환합니다.
     *
     * @param userId 댓글을 조회하는 사용자의 ID
     * @param projectId 댓글을 조회할 프로젝트의 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글, 작성자 정보, 답글 수, 좋아요 여부가 포함된 페이지 결과
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

        CommentLabelResponse result = getCommentLabelResponse(userIds);

        Page<FindCommentResponse> findCommentResponses = savedComments.map(wrapper -> {
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

        LoggerFactory.service().logSuccess("FindCommentListUseCase", "프로젝트의 댓글 목록 조회 서비스 종료 projectId=" + projectId, startTime);
        return findCommentResponses;
    }

    /**
     * 지정된 프로젝트의 원본 댓글에 대한 답글 목록을 페이지로 조회한다.
     * 각 답글에 작성자 정보(닉네임, 썸네일, 작성자 레벨 라벨)와 사용자의 좋아요 여부를 포함해 반환합니다.
     *
     * @param userId    현재 사용자의 ID
     * @param projectId 프로젝트 ID
     * @param commentId 부모 댓글 ID
     * @param pageable  페이지네이션 정보
     * @return          답글, 작성자 메타데이터, 좋아요 여부가 포함된 페이지 객체
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

        CommentLabelResponse result = getCommentLabelResponse(userIds);

        Page<FindReplyCommentResponse> findReplyCommentResponses = savedComments.map(comment -> {
            Long authorLevelId = Long.parseLong(result.userAuthorLevelIds().get(comment.getUserId()));
            return findCommentDtoMapper.toResponseDto(
                    comment,
                    result.usernameMap().get(comment.getUserId()),
                    result.userThumbnailMap().get(comment.getUserId()),
                    result.userAuthorLevelLabelMap().get(authorLevelId),
                    likedIds.contains(comment.getId())
            );
        });

        LoggerFactory.service().logSuccess("FindReplyCommentListUseCase", "프로젝트의 댓글에 대한 답글 목록 조회 서비스 종료 projectId=" + projectId + ", commentId=" + commentId, startTime);
        return findReplyCommentResponses;
    }

    /**
     * 주어진 사용자 ID 목록에 대해 사용자명, 사용자 프로필 이미지 URL, 작성자 레벨 ID, 작성자 레벨 라벨 정보를 조회하여 CommentLabelResponse로 반환합니다.
     *
     * @param userIds 정보 조회 대상인 사용자 ID 목록
     * @return 각 사용자 ID에 대한 사용자명, 사용자 프로필 이미지 URL, 작성자 레벨 ID, 작성자 레벨 라벨이 포함된 CommentLabelResponse 객체
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
}
