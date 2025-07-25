package com.dataracy.modules.project.application.port.in;

import com.dataracy.modules.project.application.dto.request.ProjectModifyRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 프로젝트 수정 유스케이스
 */
public interface ProjectModifyUseCase {
    /**
 * 지정된 프로젝트의 정보를 주어진 파일과 수정 요청 데이터로 변경합니다.
 *
 * @param projectId 수정할 프로젝트의 식별자
 * @param file 프로젝트와 관련된 파일(예: 첨부파일)
 * @param requestDto 프로젝트 수정에 필요한 상세 정보가 담긴 요청 객체
 */
void modify(Long projectId, MultipartFile file, ProjectModifyRequest requestDto);
}
