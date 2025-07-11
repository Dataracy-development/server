package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;

/**
 * 프로젝트 업로드 유스케이스
 */
public interface ProjectUploadUseCase {
    void upload(Long userId, ProjectUploadRequest requestDto);
}
