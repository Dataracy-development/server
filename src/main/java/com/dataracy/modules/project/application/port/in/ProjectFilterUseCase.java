package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.request.ProjectFilterRequest;
import com.dataracy.modules.project.application.dto.response.ProjectFilterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectFilterUseCase {
    Page<ProjectFilterResponse> findFilteringProjects(ProjectFilterRequest requestDto, Pageable pageable);
}
