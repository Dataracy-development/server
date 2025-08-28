package com.dataracy.modules.project.adapter.web.mapper.support;

import com.dataracy.modules.project.adapter.web.response.support.ProjectConnectedDataWebResponse;
import com.dataracy.modules.project.application.dto.response.support.ProjectConnectedDataResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectConnectedDataWebMapper {
    /**
     * ProjectConnectedDataResponse를 ProjectConnectedDataWebResponse로 변환합니다.
     *
     * <p>responseDto의 필드들을 1:1로 매핑하여 새로운 ProjectConnectedDataWebResponse 인스턴스를 생성합니다.
     * 널 검사나 추가 변환 로직은 수행하지 않습니다.</p>
     *
     * @param responseDto 변환할 소스 DTO
     * @return 변환된 ProjectConnectedDataWebResponse 객체
     */
    public ProjectConnectedDataWebResponse toWebDto(ProjectConnectedDataResponse responseDto) {
        return new ProjectConnectedDataWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.creatorId(),
                responseDto.creatorName(),
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
