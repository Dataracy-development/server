package com.dataracy.modules.project.application.dto.response.support;

import com.dataracy.modules.dataset.application.dto.response.read.ConnectedDataResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectConnectedDataResponse(
        Long id,
        String title,
        String topicLabel,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String dataThumbnailUrl,
        Integer downloadCount,
        Integer rowCount,
        Integer columnCount,
        LocalDateTime createdAt,
        Long countConnectedProjects
) {
    /**
     * 타 어그리거트인 ConnectedDataResponse 객체를 ProjectConnectedDataResponse로 변환합니다.
     *
     * @param data 변환할 ConnectedDataResponse 객체
     * @return 변환된 ProjectConnectedDataResponse 인스턴스
     */
    public static ProjectConnectedDataResponse from(ConnectedDataResponse data) {
        return new ProjectConnectedDataResponse(
                data.id(),
                data.title(),
                data.topicLabel(),
                data.dataTypeLabel(),
                data.startDate(),
                data.endDate(),
                data.dataThumbnailUrl(),
                data.downloadCount(),
                data.rowCount(),
                data.columnCount(),
                data.createdAt(),
                data.countConnectedProjects()
        );
    }
}
