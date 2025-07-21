package com.dataracy.modules.dataset.application.dto.response;

import java.util.Map;

public record DataLabelMappingResponse(
        Map<Long, String> usernameMap,
        Map<Long, String> topicLabelMap,
        Map<Long, String> dataSourceLabelMap,
        Map<Long, String> dataTypeLabelMap
) {}
