package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.response.ContinueProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContinueProjectUseCase {
Page<ContinueProjectResponse> findContinueProjects(Long projectId, Pageable pageable);
}
