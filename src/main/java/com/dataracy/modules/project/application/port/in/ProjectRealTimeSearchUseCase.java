package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;

import java.util.List;

public interface ProjectRealTimeSearchUseCase {
    List<ProjectRealTimeSearchResponse> search(String keyword, int size);
}
