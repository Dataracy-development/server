package com.dataracy.modules.project.adapter.web.mapper.support;

import com.dataracy.modules.project.adapter.web.response.support.ProjectConnectedDataWebResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectConnectedDataWebMapper {
    /**
     * ProjectConnectedDataResponse 객체를 ProjectConnectedDataWebResponse 객체로 변환합니다.
     *
     * @param responseDto 변환할 ProjectConnectedDataResponse 객체
     * @return 변환된 ProjectConnectedDataWebResponse 객체
     */
    public ProjectConnectedDataWebResponse toWebDto(ProjectConnectedDataResponse responseDto) {
        return new ProjectConnectedDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.userProfileImageUrl(),
                responseDto.topicLabel(),
                responseDto.dataTypeLabel(),
                responseDto.startDate(),
                responseDto.endDate(),
                responseDto.dataThumbnailUrl(),
                responseDto.downloadCount(),
                responseDto.rowCount(),
                responseDto.columnCount(),
                responseDto.createdAt(),
                responseDto.countConnectedProjects()
        );
    }
}
