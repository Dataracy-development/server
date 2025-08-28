package com.dataracy.modules.project.application.dto.response.support;

import com.dataracy.modules.dataset.application.dto.response.read.ConnectedDataResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectConnectedDataResponse(
        Long id,
        String title,
        Long creatorId,
        String creatorName,
        String userProfileImageUrl,
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
     * ConnectedDataResponse를 ProjectConnectedDataResponse로 변환하여 반환합니다.
     *
     * <p>전달된 {@code ConnectedDataResponse}의 필드를 1:1로 매핑하여 새로운 {@code ProjectConnectedDataResponse} 인스턴스를 생성합니다.
     *
     * @param data 변환할 {@code ConnectedDataResponse} 객체 (필드들은 그대로 매핑됨)
     * @return 변환된 {@code ProjectConnectedDataResponse} 인스턴스
     */
    public static ProjectConnectedDataResponse from(ConnectedDataResponse data) {
        return new ProjectConnectedDataResponse(
                data.id(),
                data.title(),
                data.creatorId(),
                data.creatorName(),
                data.userProfileImageUrl(),
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
