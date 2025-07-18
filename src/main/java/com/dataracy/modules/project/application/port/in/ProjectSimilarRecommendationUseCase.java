package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.response.ProjectSimilarSearchResponse;

import java.util.List;

public interface ProjectSimilarRecommendationUseCase {
    List<ProjectSimilarSearchResponse> findSimilarProjects(Long projectId, int size);
}
