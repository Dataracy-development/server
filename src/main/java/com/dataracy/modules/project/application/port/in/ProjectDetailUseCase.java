package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.response.ProjectDetailResponse;

public interface ProjectDetailUseCase {
    ProjectDetailResponse getProjectDetailInfo(Long projectId);
}
