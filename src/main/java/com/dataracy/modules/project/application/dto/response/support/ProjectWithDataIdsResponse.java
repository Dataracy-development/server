package com.dataracy.modules.project.application.dto.response.support;

import com.dataracy.modules.project.domain.model.Project;

import java.util.List;

public record ProjectWithDataIdsResponse(
        Project project,
        List<Long> dataIds
) {}
