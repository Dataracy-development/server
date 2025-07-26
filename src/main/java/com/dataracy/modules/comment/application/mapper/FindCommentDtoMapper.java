package com.dataracy.modules.comment.application.mapper;

import com.dataracy.modules.comment.application.dto.response.FindCommentResponse;
import com.dataracy.modules.comment.domain.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class FindCommentDtoMapper {
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
}
