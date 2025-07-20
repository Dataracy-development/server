package com.dataracy.modules.project.adapter.web.mapper;

import com.dataracy.modules.project.adapter.web.response.ChildProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.ChildProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ChildProjectWebMapper {
    /**
     * ChildProjectResponse 객체를 ChildProjectWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 ChildProjectResponse 객체
     * @return 변환된 ChildProjectWebResponse 객체
     */
    public ChildProjectWebResponse toWebDto(ChildProjectResponse responseDto) {
        return new ChildProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.username(),
                responseDto.commentCount(),
                responseDto.likeCount()
        );
    }
}
