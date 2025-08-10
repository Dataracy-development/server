package com.dataracy.modules.dataset.application.dto.response.read;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @param id
 * @param title
 * @param username
 * @param topicLabel
 * @param dataSourceLabel
 * @param dataTypeLabel
 * @param startDate
 * @param endDate
 * @param description
 * @param dataThumbnailUrl
 * @param downloadCount
 * @param rowCount
 * @param columnCount
 * @param createdAt
 * @param countConnectedProjects
 */
public record PopularDataResponse(
        Long id,
        String title,
        String username,
        String topicLabel,
        String dataSourceLabel,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String dataThumbnailUrl,
        Integer downloadCount,
        Integer rowCount,
        Integer columnCount,
        LocalDateTime createdAt,
        Long countConnectedProjects
) {}
