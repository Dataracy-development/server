package com.dataracy.modules.project.application.port.in.command.content;

import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ModifyProjectUseCase {
    /**
     * 프로젝트의 정보를 수정 요청 데이터와 썸네일 파일을 기반으로 갱신합니다.
     *
     * @param projectId 수정할 프로젝트의 고유 식별자
     * @param thumbnailFile 프로젝트에 첨부할 썸네일 이미지 파일
     * @param requestDto 프로젝트 수정에 필요한 상세 정보가 담긴 요청 객체
     */
    void modifyProject(Long projectId, MultipartFile thumbnailFile, ModifyProjectRequest requestDto);
}
