package com.dataracy.modules.project.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.project.adapter.web.mapper.ProjectWebMapper;
import com.dataracy.modules.project.adapter.web.request.ProjectUploadWebRequest;
import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import com.dataracy.modules.project.application.port.in.ProjectUploadUseCase;
import com.dataracy.modules.project.domain.status.ProjectSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ProjectController implements ProjectApi {
    private final ProjectWebMapper projectWebMapper;

    private final ProjectUploadUseCase projectUploadUseCase;

    /**
     * 사용자의 프로젝트 업로드 요청을 처리하여 프로젝트를 생성한다.
     *
     * @param userId 프로젝트를 업로드하는 사용자의 ID
     * @param imageFile 프로젝트 썸네일 이미지 파일
     * @param webRequest 업로드할 프로젝트 정보가 담긴 요청 객체
     * @return 프로젝트가 성공적으로 생성되었음을 알리는 201 Created 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> uploadProject(
            Long userId,
            MultipartFile imageFile,
            ProjectUploadWebRequest webRequest
    ) {
        ProjectUploadRequest requestDto = projectWebMapper.toApplicationDto(webRequest);
        projectUploadUseCase.upload(userId, imageFile, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(ProjectSuccessStatus.CREATED_PROJECT));
    }
}
