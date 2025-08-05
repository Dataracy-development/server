package com.dataracy.modules.project.application.port.in.query.extractor;

import com.dataracy.modules.project.application.dto.response.support.ProjectLabelMapResponse;
import com.dataracy.modules.project.domain.model.Project;

import java.util.Collection;

public interface FindProjectLabelMapUseCase {
    ProjectLabelMapResponse labelMapping(Collection<Project> savedProjects);
}
