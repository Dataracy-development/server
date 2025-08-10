package com.dataracy.modules.project.application.dto.request.search;

/**
 *
 * @param keyword
 * @param sortType
 * @param topicId
 * @param analysisPurposeId
 * @param dataSourceId
 * @param authorLevelId
 */
public record FilteringProjectRequest(
        String keyword,
        String sortType,
        Long topicId,
        Long analysisPurposeId,
        Long dataSourceId,
        Long authorLevelId
) {}
