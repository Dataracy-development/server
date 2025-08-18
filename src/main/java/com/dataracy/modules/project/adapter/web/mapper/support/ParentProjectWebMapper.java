package com.dataracy.modules.project.adapter.web.mapper.support;

import com.dataracy.modules.project.adapter.web.response.support.ParentProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.support.ParentProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ParentProjectWebMapper {
    /**
     * ParentProjectResponse 객체를 ParentProjectWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 ParentProjectResponse 객체
     * @return 변환된 ParentProjectWebResponse 객체
     */
    public ParentProjectWebResponse toWebDto(ParentProjectResponse responseDto) {
        return new ParentProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.username(),
                responseDto.commentCount(),
                responseDto.likeCount(),
                responseDto.viewCount(),
                responseDto.createdAt()
        );
    }
}
