package com.dataracy.modules.project.application.port.query;

import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;

import java.util.List;

public interface ProjectSearchQueryPort {
    List<ProjectRealTimeSearchResponse> search(String keyword, int size);
}
