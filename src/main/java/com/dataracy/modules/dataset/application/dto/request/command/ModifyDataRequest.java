package com.dataracy.modules.dataset.application.dto.request.command;

import java.time.LocalDate;

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
