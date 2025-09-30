package com.dataracy.modules.project.adapter.web.mapper.support;

import com.dataracy.modules.project.adapter.web.response.support.ParentProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ParentProjectWebMapper {
    /**
     * ParentProjectResponse를 ParentProjectWebResponse로 변환합니다.
     *
     * 입력이 null이면 null을 반환합니다. 다음 필드를 1:1로 복사하여 새 웹 응답 DTO를 생성합니다:
     * id, title, content, creatorId, creatorName, commentCount, likeCount, viewCount, createdAt.
     *
     * @param responseDto 변환할 도메인 응답 객체 (nullable)
     * @return 변환된 ParentProjectWebResponse 객체, 입력이 null이면 null
     */
    public ParentProjectWebResponse toWebDto(ParentProjectResponse responseDto) {
        if (responseDto == null) {
            return null;
        }

        return new ParentProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.commentCount(),
                responseDto.likeCount(),
                responseDto.viewCount(),
                responseDto.createdAt()
        );
    }
}
