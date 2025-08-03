package com.dataracy.modules.project.application.port.in.command.content;

import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ModifyProjectUseCase {
    /**
     * 지정된 프로젝트의 정보를 주어진 파일과 수정 요청 데이터로 변경합니다.
     *
     * @param projectId 수정할 프로젝트의 식별자
     * @param file 프로젝트와 관련된 파일(예: 첨부파일)
     * @param requestDto 프로젝트 수정에 필요한 상세 정보가 담긴 요청 객체
     */
    void modifyProject(Long projectId, MultipartFile file, ModifyProjectRequest requestDto);
}
