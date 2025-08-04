package com.dataracy.modules.dataset.application.dto.request.search;

public record FilteringDataRequest(
        String keyword,
        String sortType,
        Long topicId,
        Long dataSourceId,
        Long dataTypeId,
        Integer year
) {}
