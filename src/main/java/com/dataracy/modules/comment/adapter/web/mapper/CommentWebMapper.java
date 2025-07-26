package com.dataracy.modules.comment.adapter.web.mapper;

import com.dataracy.modules.comment.adapter.web.request.CommentModifyWebRequest;
import com.dataracy.modules.comment.adapter.web.request.CommentUploadWebRequest;
import com.dataracy.modules.comment.adapter.web.response.FindCommentWebResponse;
import com.dataracy.modules.comment.application.dto.request.CommentModifyRequest;
import com.dataracy.modules.comment.application.dto.request.CommentUploadRequest;
import com.dataracy.modules.comment.application.dto.response.FindCommentResponse;
import org.springframework.stereotype.Component;

@Component
public class CommentWebMapper {
    public CommentUploadRequest toApplicationDto(CommentUploadWebRequest webRequest) {
        return new CommentUploadRequest(
                webRequest.content(),
                webRequest.parentCommentId()
        );
    }

    public CommentModifyRequest toApplicationDto(CommentModifyWebRequest webRequest) {
        return new CommentModifyRequest(
                webRequest.content()
        );
    }

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
}
