package com.dataracy.modules.user.adapter.web.mapper.support;

import com.dataracy.modules.user.adapter.web.response.support.OtherUserDataWebResponse;
import com.dataracy.modules.user.adapter.web.response.support.OtherUserProjectWebResponse;
import com.dataracy.modules.user.application.dto.response.support.OtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.support.OtherUserProjectResponse;
import org.springframework.stereotype.Component;

/**
 * 타 어그리거트로부터 받아 변환한 유저 정보를 토대로 한 애플리케이션 DTO와 웹 DTO를 변환하는 매퍼
 */
@Component
public class OtherUserInfoWebMapper {

    public OtherUserProjectWebResponse toWebDto(OtherUserProjectResponse responseDto) {
        return new OtherUserProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.projectThumbnailUrl(),
                responseDto.topicLabel(),
                responseDto.authorLevelLabel(),
                responseDto.commentCount(),
                responseDto.likeCount(),
                responseDto.viewCount(),
                responseDto.createdAt()
        );
    }

    public OtherUserDataWebResponse toWebDto(OtherUserDataResponse responseDto) {
        return new OtherUserDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.topicLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.sizeBytes(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }
}
