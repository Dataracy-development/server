package com.dataracy.modules.project.application.dto.response.support;

import com.dataracy.modules.project.domain.model.Project;

import java.util.List;

/**
 *
 * @param project
 * @param dataIds
 */
public record ProjectWithDataIdsResponse(
        Project project,
        List<Long> dataIds
) {}
