package com.dataracy.modules.dataset.application.dto.response.read;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *요청
 * @param id
 * @param title
 * @param username
 * @param authorLabel
 * @param occupationLabel
 * @param topicLabel
 * @param dataSourceLabel
 * @param dataTypeLabel
 * @param startDate
 * @param endDate
 * @param description
 * @param analysisGuide
 * @param dataThumbnailUrl
 * @param downloadCount
 * @param rowCount
 * @param columnCount
 * @param previewJson
 * @param createdAt
 */
public record DataDetailResponse(
        Long id,
        String title,
        String username,
        String authorLabel,
        String occupationLabel,
        String topicLabel,
        String dataSourceLabel,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String analysisGuide,
        String dataThumbnailUrl,
        Integer downloadCount,
        Integer rowCount,
        Integer columnCount,
        String previewJson,
        LocalDateTime createdAt
) {}
