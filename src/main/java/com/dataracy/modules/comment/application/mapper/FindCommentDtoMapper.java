package com.dataracy.modules.comment.application.mapper;

import com.dataracy.modules.comment.application.dto.response.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.FindReplyCommentResponse;
import com.dataracy.modules.comment.domain.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class FindCommentDtoMapper {
    /**
     * Comment 도메인 객체와 사용자 정보를 기반으로 FindCommentResponse DTO로 변환합니다.
     *
     * @param comment 변환할 댓글 도메인 객체
     * @param username 댓글 작성자의 사용자명
     * @param userThumbnailUrl 댓글 작성자의 프로필 이미지 URL
     * @param authorLevelLabel 댓글 작성자의 레벨 라벨
     * @param childCommentCount 해당 댓글의 자식 댓글 수
     * @return 댓글 및 사용자 정보가 포함된 FindCommentResponse DTO
     */
    public FindCommentResponse toResponseDto(
            Comment comment,
            String username,
            String userThumbnailUrl,
            String authorLevelLabel,
            Long childCommentCount
    ) {
        return new FindCommentResponse(
                comment.getId(),
                username,
                authorLevelLabel,
                userThumbnailUrl,
                comment.getContent(),
                comment.getLikeCount(),
                childCommentCount,
                comment.getCreatedAt()
        );
    }

    /**
     * 댓글 도메인 객체와 사용자 정보를 기반으로 답글 댓글 응답 DTO로 변환합니다.
     *
     * @param comment 변환할 댓글 도메인 객체
     * @param username 댓글 작성자의 사용자명
     * @param userThumbnailUrl 댓글 작성자의 프로필 이미지 URL
     * @param authorLevelLabel 댓글 작성자의 등급 라벨
     * @return 답글 댓글 응답 DTO
     */
    public FindReplyCommentResponse toResponseDto(
            Comment comment,
            String username,
            String userThumbnailUrl,
            String authorLevelLabel
    ) {
        return new FindReplyCommentResponse(
                comment.getId(),
                username,
                authorLevelLabel,
                userThumbnailUrl,
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt()
        );
    }
}
