package com.dataracy.modules.dataset.application.dto.response.search;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @param id
 * @param title
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
public record FilteredDataResponse(
        Long id,
        String title,
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
