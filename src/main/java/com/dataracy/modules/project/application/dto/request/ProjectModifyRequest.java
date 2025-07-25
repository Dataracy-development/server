package com.dataracy.modules.project.application.dto.request;

import java.util.List;

public record ProjectModifyRequest(
        String title,
        Long topicId,
        Long analysisPurposeId,
        Long dataSourceId,
        Long authorLevelId,
        Boolean isContinue,
        Long parentProjectId,
        String content,
        List<Long> dataIds
) {}
