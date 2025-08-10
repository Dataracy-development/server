package com.dataracy.modules.dataset.application.dto.response.support;

import java.util.Map;

/**
 *
 * @param usernameMap
 * @param topicLabelMap
 * @param dataSourceLabelMap
 * @param dataTypeLabelMap
 */
public record DataLabelMapResponse(
        Map<Long, String> usernameMap,
        Map<Long, String> topicLabelMap,
        Map<Long, String> dataSourceLabelMap,
        Map<Long, String> dataTypeLabelMap
) {}
