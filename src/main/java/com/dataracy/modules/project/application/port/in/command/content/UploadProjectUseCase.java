package com.dataracy.modules.project.application.port.in.command.content;

import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UploadProjectUseCase {
    /**
 * 지정된 사용자의 프로젝트와 썸네일 이미지를 업로드합니다.
 *
 * @param userId 프로젝트를 업로드할 사용자 ID
 * @param thumbnailFile 업로드할 프로젝트 썸네일 파일
 * @param requestDto 프로젝트의 상세 정보가 포함된 요청 DTO
 */
    void uploadProject(Long userId, MultipartFile thumbnailFile, UploadProjectRequest requestDto);
}
