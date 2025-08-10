package com.dataracy.modules.project.application.dto.request.command;

import java.util.List;

/**
 *요청
 * @param title
 * @param topicId
 * @param analysisPurposeId
 * @param dataSourceId
 * @param authorLevelId
 * @param isContinue
 * @param parentProjectId
 * @param content
 * @param dataIds
 */
public record ModifyProjectRequest(
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
