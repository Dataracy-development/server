package com.dataracy.modules.project.adapter.web.mapper;

import com.dataracy.modules.project.adapter.web.response.ChildProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.ChildProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ChildProjectWebMapper {
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
