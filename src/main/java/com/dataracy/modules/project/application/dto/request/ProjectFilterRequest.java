package com.dataracy.modules.project.application.dto.request;

public record ProjectFilterRequest(
        String keyword,
        String sortType,
        Long topicId,
        Long analysisPurposeId,
        Long dataSourceId,
        Long authorLevelId
) {}
