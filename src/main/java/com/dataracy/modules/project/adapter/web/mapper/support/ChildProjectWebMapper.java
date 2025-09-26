package com.dataracy.modules.project.adapter.web.mapper.support;

import com.dataracy.modules.project.adapter.web.response.support.ChildProjectWebResponse;
import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;
import org.springframework.stereotype.Component;

@Component
public class ChildProjectWebMapper {
    /**
     * ChildProjectResponse를 ChildProjectWebResponse로 변환합니다.
     *
     * 입력된 응답 DTO의 각 필드를 대응하는 웹 응답 객체의 필드로 직접 매핑하여 새 ChildProjectWebResponse 인스턴스를 생성합니다.
     * responseDto는 null이 아니어야 합니다.
     *
     * @param responseDto 변환할 원본 ChildProjectResponse 객체 (null이면 동작 보장되지 않음)
     * @return 변환된 ChildProjectWebResponse 객체
     */
    public ChildProjectWebResponse toWebDto(ChildProjectResponse responseDto) {
        return new ChildProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.commentCount(),
                responseDto.likeCount()
        );
    }
}
