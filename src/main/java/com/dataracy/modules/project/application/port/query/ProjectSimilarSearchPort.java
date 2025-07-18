package com.dataracy.modules.project.application.port.query;

import com.dataracy.modules.project.application.dto.response.ProjectSimilarSearchResponse;
import com.dataracy.modules.project.domain.model.Project;

import java.util.List;

public interface ProjectSimilarSearchPort {
    List<ProjectSimilarSearchResponse> recommendSimilarProjects(Project project, int size);
}
