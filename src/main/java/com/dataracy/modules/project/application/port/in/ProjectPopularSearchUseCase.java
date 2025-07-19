package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.response.ProjectPopularSearchResponse;

import java.util.List;

public interface ProjectPopularSearchUseCase {
    List<ProjectPopularSearchResponse> findPopularProjects(int size);
}
