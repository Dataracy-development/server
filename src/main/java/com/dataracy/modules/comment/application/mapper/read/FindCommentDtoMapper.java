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
     * 댓글 도메인 객체와 사용자 정보를 기반으로 FindCommentResponse DTO로 변환합니다.
     *
     * @param comment 변환할 댓글 도메인 객체
     * @param username 댓글 작성자의 사용자명
     * @param userProfileImageUrl 댓글 작성자의 프로필 이미지 URL
     * @param authorLevelLabel 댓글 작성자의 레벨 라벨
     * @param childCommentCount 해당 댓글의 답글 수
     * @param isLiked 사용자가 해당 댓글을 좋아요 했는지 여부
     * @return 댓글 정보, 작성자 정보, 자식 댓글 수, 좋아요 여부가 포함된 FindCommentResponse DTO
     */
    public FindCommentResponse toResponseDto(
            Comment comment,
            String username,
            String userProfileImageUrl,
            String authorLevelLabel,
            Long childCommentCount,
            boolean isLiked
    ) {
        return new FindCommentResponse(
                comment.getId(),
                comment.getUserId(),
                username,
                userProfileImageUrl,
                authorLevelLabel,
                comment.getContent(),
                comment.getLikeCount(),
                childCommentCount,
                comment.getCreatedAt(),
                isLiked
        );
    }

    /**
     * 답글 도메인 객체와 사용자 정보를 답글 응답 DTO로 변환합니다.
     *
     * @param comment 변환할 답글 도메인 객체
     * @param username 답글 작성자의 사용자명
     * @param userProfileImageUrl 답글 작성자의 프로필 이미지 URL
     * @param authorLevelLabel 답글 작성자의 등급 라벨
     * @param isLiked 사용자가 해당 답글에 좋아요를 눌렀는지 여부
     * @return 답글 정보를 담은 FindReplyCommentResponse 객체
     */
    public FindReplyCommentResponse toResponseDto(
            Comment comment,
            String username,
            String userProfileImageUrl,
            String authorLevelLabel,
            boolean isLiked
    ) {
        return new FindReplyCommentResponse(
                comment.getId(),
                comment.getUserId(),
                username,
                userProfileImageUrl,
                authorLevelLabel,
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt(),
                isLiked
        );
    }
}
