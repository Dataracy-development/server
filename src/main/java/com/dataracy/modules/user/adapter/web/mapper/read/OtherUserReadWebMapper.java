package com.dataracy.modules.user.adapter.web.mapper.read;

import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserDataWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserInfoWebResponse;
import com.dataracy.modules.user.adapter.web.response.read.GetOtherUserProjectWebResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserDataResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserInfoResponse;
import com.dataracy.modules.user.application.dto.response.read.GetOtherUserProjectResponse;
import org.springframework.stereotype.Component;

/**
 * 타 어그리거트로부터 받아 변환한 유저 정보를 토대로 한 애플리케이션 DTO와 웹 DTO를 변환하는 매퍼
 */
@Component
public class OtherUserReadWebMapper {
    public GetOtherUserInfoWebResponse toWebDto(GetOtherUserInfoResponse responseDto) {
        return new GetOtherUserInfoWebResponse(
                responseDto.id(),
                responseDto.nickname(),
                responseDto.authorLevelLabel(),
                responseDto.occupationLabel(),
                responseDto.profileImageUrl(),
                responseDto.introductionText(),
                responseDto.projects()
                        .map(this::toWebDto),
                responseDto.datasets()
                        .map(this::toWebDto)
        );
    }

    public GetOtherUserProjectWebResponse toWebDto(GetOtherUserProjectResponse responseDto) {
        return new GetOtherUserProjectWebResponse(
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

    public GetOtherUserDataWebResponse toWebDto(GetOtherUserDataResponse responseDto) {
        return new GetOtherUserDataWebResponse(
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
