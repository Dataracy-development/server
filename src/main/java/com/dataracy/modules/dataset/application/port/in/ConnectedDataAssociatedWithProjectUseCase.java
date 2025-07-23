package com.dataracy.modules.dataset.application.port.in;

import com.dataracy.modules.dataset.application.dto.response.ConnectedDataAssociatedWithProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConnectedDataAssociatedWithProjectUseCase {
Page<ConnectedDataAssociatedWithProjectResponse> findConnectedDataSetsAssociatedWithProject(Long projectId, Pageable pageable);
}
