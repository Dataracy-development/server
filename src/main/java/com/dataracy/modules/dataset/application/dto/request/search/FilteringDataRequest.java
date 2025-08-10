package com.dataracy.modules.dataset.application.dto.request.search;

/**
 *요청
 * @param keyword
 * @param sortType
 * @param topicId
 * @param dataSourceId
 * @param dataTypeId
 * @param year
 */
public record FilteringDataRequest(
        String keyword,
        String sortType,
        Long topicId,
        Long dataSourceId,
        Long dataTypeId,
        Integer year
) {}
