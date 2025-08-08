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
