package com.dataracy.modules.dataset.application.dto.request;

public record DataFilterRequest(
        String keyword,
        String sortType,
        Long topicId,
        Long dataSourceId,
        Long dataTypeId,
        Integer year
) {}
