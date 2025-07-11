package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 프로젝트 업로드 유스케이스
 */
public interface ProjectUploadUseCase {
    /**
     * 지정된 사용자의 프로젝트를 업로드합니다.
     *
     * @param userId 프로젝트를 업로드할 사용자 ID
     * @param imageFile 썸네일 이미지 파일
     * @param requestDto 업로드할 프로젝트의 정보가 담긴 요청 객체
     */
    void upload(Long userId, MultipartFile imageFile, ProjectUploadRequest requestDto);
}
