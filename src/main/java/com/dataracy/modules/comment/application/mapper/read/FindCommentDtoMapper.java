package com.dataracy.modules.comment.application.mapper.read;

import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import com.dataracy.modules.comment.domain.model.Comment;
import org.springframework.stereotype.Component;

/**
 * 댓글 도메인 모델과 애플리케이션 응답 DTO를 변환하는 매퍼
 */
@Component
public class FindCommentDtoMapper {
    /**
     * 댓글 도메인 객체와 관련 메타데이터를 사용해 FindCommentResponse DTO로 변환합니다.
     *
     * 변환된 DTO에는 댓글 ID, 작성자 사용자ID, 작성자명, 작성자 레벨 라벨, 프로필 URL, 내용, 좋아요 수,
     * 자식 댓글 수, 생성 시각 및 현재 사용자의 좋아요 여부가 포함됩니다.
     *
     * @param comment 변환 대상 댓글 도메인 객체
     * @param username 댓글 작성자의 표시 이름
     * @param userProfileUrl 댓글 작성자의 프로필 이미지 URL
     * @param authorLevelLabel 댓글 작성자의 레벨 라벨
     * @param childCommentCount 해당 댓글의 자식(답글) 개수
     * @param isLiked 현재 사용자가 해당 댓글을 좋아요했는지 여부
     * @return 댓글 및 작성자 정보, 통계와 상태를 포함한 FindCommentResponse DTO
     */
    public FindCommentResponse toResponseDto(
            Comment comment,
            String username,
            String userProfileUrl,
            String authorLevelLabel,
            Long childCommentCount,
            boolean isLiked
    ) {
        return new FindCommentResponse(
                comment.getId(),
                comment.getUserId(),
                username,
                authorLevelLabel,
                userProfileUrl,
                comment.getContent(),
                comment.getLikeCount(),
                childCommentCount,
                comment.getCreatedAt(),
                isLiked
        );
    }

    /**
     * 답글(Comment) 도메인 객체와 사용자 메타데이터를 FindReplyCommentResponse DTO로 변환합니다.
     *
     * <p>변환된 DTO에는 댓글의 식별자(id)와 해당 작성자(userId), 작성자명, 등급 라벨, 프로필 URL,
     * 내용, 좋아요 수, 생성 시간 및 현재 사용자의 좋아요 여부(isLiked)가 포함됩니다.</p>
     *
     * @param comment 변환할 답글 도메인 객체
     * @param username 답글 작성자의 사용자명
     * @param userProfileUrl 답글 작성자의 프로필 이미지 URL
     * @param authorLevelLabel 답글 작성자의 등급 라벨
     * @param isLiked 현재 사용자가 해당 답글에 좋아요를 눌렀는지 여부
     * @return 변환된 FindReplyCommentResponse DTO
     */
    public FindReplyCommentResponse toResponseDto(
            Comment comment,
            String username,
            String userProfileUrl,
            String authorLevelLabel,
            boolean isLiked
    ) {
        return new FindReplyCommentResponse(
                comment.getId(),
                comment.getUserId(),
                username,
                authorLevelLabel,
                userProfileUrl,
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt(),
                isLiked
        );
    }
}
