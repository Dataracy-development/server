package com.dataracy.modules.project.application.port.in.command.content;

import com.dataracy.modules.project.application.dto.request.command.UploadProjectRequest;
import com.dataracy.modules.project.application.dto.response.command.UploadProjectResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UploadProjectUseCase {
    /**
 * 지정된 사용자의 프로젝트 정보와 해당 썸네일 이미지를 업로드하고 업로드 결과를 반환합니다.
 *
 * @param userId 업로드할 프로젝트의 소유자(사용자) ID
 * @param thumbnailFile 프로젝트 썸네일로 업로드할 파일(MultipartFile)
 * @param requestDto 프로젝트의 제목·설명 등 업로드에 필요한 상세 정보가 담긴 요청 DTO
 * @return 업로드 결과를 담은 {@link UploadProjectResponse}
 */
    UploadProjectResponse uploadProject(Long userId, MultipartFile thumbnailFile, UploadProjectRequest requestDto);
}
