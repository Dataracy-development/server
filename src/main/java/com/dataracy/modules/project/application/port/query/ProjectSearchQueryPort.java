package com.dataracy.modules.project.application.port.query;

import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.dto.response.ProjectSimilarSearchResponse;
import com.dataracy.modules.project.domain.model.Project;

import java.util.List;

public interface ProjectSearchQueryPort {
List<ProjectRealTimeSearchResponse> search(String keyword, int size);
    List<ProjectSimilarSearchResponse> recommendSimilarProjects(Project project, int size);
}
