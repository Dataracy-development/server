package com.dataracy.modules.comment.adapter.web.mapper.read;

import com.dataracy.modules.comment.adapter.web.response.read.FindCommentWebResponse;
import com.dataracy.modules.comment.adapter.web.response.read.FindReplyCommentWebResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.read.FindReplyCommentResponse;
import org.springframework.stereotype.Component;

@Component
public class ReadCommentWebMapper {
    /**
     * 애플리케이션 계층의 댓글 응답 DTO를 웹 계층의 댓글 응답 객체로 변환합니다.
     *
     * @param responseDto 변환할 댓글 응답 DTO
     * @return 댓글 ID, 작성자 정보, 프로필 이미지 URL, 내용, 좋아요 수, 대댓글 수, 생성 시각, 좋아요 여부를 포함한 웹 계층 댓글 응답 객체
     */
    public FindCommentWebResponse toWebDto(FindCommentResponse responseDto) {
        return new FindCommentWebResponse(
                responseDto.id(),
                responseDto.username(),
                responseDto.authorLevelLabel(),
                responseDto.userProfileUrl(),
                responseDto.content(),
                responseDto.likeCount(),
                responseDto.childCommentCount(),
                responseDto.createdAt(),
                responseDto.isLiked()
        );
    }

    /**
     * 애플리케이션 계층의 답글 댓글 응답 DTO를 웹 계층의 답글 댓글 응답 객체로 변환합니다.
     *
     * @param responseDto 변환할 답글 댓글 응답 DTO
     * @return 변환된 웹 계층의 답글 댓글 응답 객체
     */
    public FindReplyCommentWebResponse toWebDto(FindReplyCommentResponse responseDto) {
        return new FindReplyCommentWebResponse(
                responseDto.id(),
                responseDto.username(),
                responseDto.authorLevelLabel(),
                responseDto.userProfileUrl(),
                responseDto.content(),
                responseDto.likeCount(),
                responseDto.createdAt(),
                responseDto.isLiked()
        );
    }
}
