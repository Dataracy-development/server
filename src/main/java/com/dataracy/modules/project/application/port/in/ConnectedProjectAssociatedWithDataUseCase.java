package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.response.ConnectedProjectAssociatedWithDataResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConnectedProjectAssociatedWithDataUseCase {
Page<ConnectedProjectAssociatedWithDataResponse> findConnectedProjects(Long dataId, Pageable pageable);
}
