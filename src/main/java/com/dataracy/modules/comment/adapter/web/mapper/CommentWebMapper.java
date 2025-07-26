package com.dataracy.modules.comment.adapter.web.mapper;

import com.dataracy.modules.comment.adapter.web.request.CommentModifyWebRequest;
import com.dataracy.modules.comment.adapter.web.request.CommentUploadWebRequest;
import com.dataracy.modules.comment.adapter.web.response.FindCommentWebResponse;
import com.dataracy.modules.comment.adapter.web.response.FindReplyCommentWebResponse;
import com.dataracy.modules.comment.application.dto.request.CommentModifyRequest;
import com.dataracy.modules.comment.application.dto.request.CommentUploadRequest;
import com.dataracy.modules.comment.application.dto.response.FindCommentResponse;
import com.dataracy.modules.comment.application.dto.response.FindReplyCommentResponse;
import org.springframework.stereotype.Component;

@Component
public class CommentWebMapper {
    /**
     * 웹 요청 객체를 애플리케이션 계층의 댓글 업로드 요청 DTO로 변환합니다.
     *
     * @param webRequest 댓글 업로드 웹 요청 객체
     * @return 변환된 댓글 업로드 요청 DTO
     */
    public CommentUploadRequest toApplicationDto(CommentUploadWebRequest webRequest) {
        return new CommentUploadRequest(
                webRequest.content(),
                webRequest.parentCommentId()
        );
    }

    /**
     * 웹 요청 객체를 애플리케이션 계층의 댓글 수정 요청 DTO로 변환합니다.
     *
     * @param webRequest 댓글 수정 웹 요청 객체
     * @return 댓글 수정 요청 DTO
     */
    public CommentModifyRequest toApplicationDto(CommentModifyWebRequest webRequest) {
        return new CommentModifyRequest(
                webRequest.content()
        );
    }

    /**
     * 애플리케이션 계층의 댓글 응답 DTO를 웹 계층의 댓글 응답 객체로 변환합니다.
     *
     * @param responseDto 변환할 댓글 응답 DTO
     * @return 변환된 웹 계층 댓글 응답 객체
     */
    public FindCommentWebResponse toWebDto(FindCommentResponse responseDto) {
        return new FindCommentWebResponse(
                responseDto.id(),
                responseDto.username(),
                responseDto.authorLevelLabel(),
                responseDto.userThumbnailUrl(),
                responseDto.content(),
                responseDto.likeCount(),
                responseDto.childCommentCount(),
                responseDto.createdAt()
        );
    }

    /**
     * 답글 댓글 응답 DTO를 웹 응답 객체로 변환합니다.
     *
     * @param responseDto 애플리케이션 계층의 답글 댓글 응답 DTO
     * @return 웹 계층의 답글 댓글 응답 객체
     */
    public FindReplyCommentWebResponse toWebDto(FindReplyCommentResponse responseDto) {
        return new FindReplyCommentWebResponse(
                responseDto.id(),
                responseDto.username(),
                responseDto.authorLevelLabel(),
                responseDto.userThumbnailUrl(),
                responseDto.content(),
                responseDto.likeCount(),
                responseDto.createdAt()
        );
    }
}
