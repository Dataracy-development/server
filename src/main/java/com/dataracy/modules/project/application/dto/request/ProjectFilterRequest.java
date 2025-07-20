package com.dataracy.modules.project.application.dto.request;

import com.dataracy.modules.project.domain.enums.ProjectSortType;

public record ProjectFilterRequest(
        String keyword,
        ProjectSortType sortType,
        Long topicId,
        Long analysisPurposeId,
        Long dataSourceId,
        Long authorLevelId
) {}
