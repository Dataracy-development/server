package com.dataracy.modules.project.application.dto.response.support;

import java.util.Map;

/**
 *
 * @param usernameMap
 * @param topicLabelMap
 * @param analysisPurposeLabelMap
 * @param dataSourceLabelMap
 * @param authorLevelLabelMap
 */
public record ProjectLabelMapResponse(
        Map<Long, String> usernameMap,
        Map<Long, String> topicLabelMap,
        Map<Long, String> analysisPurposeLabelMap,
        Map<Long, String> dataSourceLabelMap,
        Map<Long, String> authorLevelLabelMap
) {}
