package com.dataracy.modules.project.application.dto.request.command;

import java.util.List;

public record UploadProjectRequest(
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
