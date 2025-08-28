package com.dataracy.modules.comment.adapter.web.mapper.read;

import com.dataracy.modules.comment.adapter.web.response.read.FindCommentWebResponse;
import com.dataracy.modules.comment.adapter.web.response.read.FindReplyCommentWebResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import org.springframework.stereotype.Component;

/**
 * 댓글 조회 웹 DTO와 애플리케이션 DTO를 변환하는 매퍼
 */
@Component
public class ReadCommentWebMapper {
    /**
     * 애플리케이션 계층의 FindCommentResponse를 웹 계층의 FindCommentWebResponse로 변환합니다.
     *
     * @param responseDto 변환할 애플리케이션 계층 댓글 응답 DTO
     * @return 댓글 ID, 작성자 정보, 프로필 이미지 URL, 작성자 레벨 레이블, 내용, 좋아요 수, 대댓글 수, 생성 시각 및 좋아요 여부를 포함한 웹 계층 댓글 응답 객체
     */
    public FindCommentWebResponse toWebDto(FindCommentResponse responseDto) {
        return new FindCommentWebResponse(
                responseDto.id(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.authorLevelLabel(),
                responseDto.content(),
                responseDto.likeCount(),
                responseDto.childCommentCount(),
                responseDto.createdAt(),
                responseDto.isLiked()
        );
    }

    /**
     * 애플리케이션 계층의 FindReplyCommentResponse를 웹 계층의 FindReplyCommentWebResponse로 변환합니다.
     *
     * 매핑되는 필드: id, creatorId, creatorName, authorLevelLabel, userProfileUrl, content, likeCount, createdAt, isLiked.
     *
     * @param responseDto 변환할 애플리케이션 계층의 답글 응답 DTO
     * @return 변환된 웹 계층의 답글 응답 객체
     */
    public FindReplyCommentWebResponse toWebDto(FindReplyCommentResponse responseDto) {
        return new FindReplyCommentWebResponse(
                responseDto.id(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.authorLevelLabel(),
                responseDto.content(),
                responseDto.likeCount(),
                responseDto.createdAt(),
                responseDto.isLiked()
        );
    }
}
