package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.request.ProjectModifyRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 프로젝트 수정 유스케이스
 */
public interface ProjectModifyUseCase {
    void modify(Long projectId, MultipartFile file, ProjectModifyRequest requestDto);
}
