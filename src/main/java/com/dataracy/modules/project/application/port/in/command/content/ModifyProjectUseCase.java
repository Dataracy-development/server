package com.dataracy.modules.project.application.port.in.command.content;

import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ModifyProjectUseCase {
    /**
 * 프로젝트의 정보를 수정 요청 데이터와 파일을 기반으로 갱신합니다.
 *
 * @param projectId 수정 대상 프로젝트의 고유 식별자
 * @param file 프로젝트에 첨부할 파일
 * @param requestDto 프로젝트 수정에 필요한 상세 정보가 포함된 요청 객체
 */
    void modifyProject(Long projectId, MultipartFile file, ModifyProjectRequest requestDto);
}
