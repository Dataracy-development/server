package com.dataracy.modules.project.application.dto.request.search;

public record FilteringProjectRequest(
        String keyword,
        String sortType,
        Long topicId,
        Long analysisPurposeId,
        Long dataSourceId,
        Long authorLevelId
) {}
