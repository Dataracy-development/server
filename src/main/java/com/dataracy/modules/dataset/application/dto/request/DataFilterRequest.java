package com.dataracy.modules.dataset.application.dto.request;

import com.dataracy.modules.dataset.domain.enums.DataSortType;

public record DataFilterRequest(
        String keyword,
        DataSortType sortType,
        Long topicId,
        Long dataSourceId,
        Long dataTypeId,
        Integer year
) {}
