package com.dataracy.modules.project.application.dto.response;

import java.util.Map;

public record ProjectLabelMappingResponse(
        Map<Long, String> usernameMap,
        Map<Long, String> topicLabelMap,
        Map<Long, String> analysisPurposeLabelMap,
        Map<Long, String> dataSourceLabelMap,
        Map<Long, String> authorLevelLabelMap
) {}
