package com.dataracy.modules.dataset.application.dto.request.command;

import java.time.LocalDate;

/**
 *
 * @param title
 * @param topicId
 * @param dataSourceId
 * @param dataTypeId
 * @param startDate
 * @param endDate
 * @param description
 * @param analysisGuide
 */
public record ModifyDataRequest(
        String title,
        Long topicId,
        Long dataSourceId,
        Long dataTypeId,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String analysisGuide
) {}
