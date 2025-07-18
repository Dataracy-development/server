package com.dataracy.modules.project.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.project.adapter.web.mapper.ProjectSearchWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.ProjectWebMapper;
import com.dataracy.modules.project.adapter.web.request.ProjectUploadWebRequest;
import com.dataracy.modules.project.adapter.web.response.ProjectRealTimeSearchWebResponse;
import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.port.in.ProjectRealTimeSearchUseCase;
import com.dataracy.modules.project.application.port.in.ProjectUploadUseCase;
import com.dataracy.modules.project.domain.status.ProjectSuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController implements ProjectApi {
    private final ProjectWebMapper projectWebMapper;
    private final ProjectSearchWebMapper projectSearchWebMapper;

    private final ProjectUploadUseCase projectUploadUseCase;
    private final ProjectRealTimeSearchUseCase projectRealTimeSearchUseCase;

    /**
     * 프로젝트 업로드 요청을 받아 새로운 프로젝트를 생성한다.
     *
     * @param userId 프로젝트를 업로드하는 사용자의 ID
     * @param file 프로젝트 썸네일 이미지 파일
     * @param webRequest 업로드할 프로젝트 정보가 담긴 요청 객체
     * @return 프로젝트 생성 성공 시 201 Created 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Void>> uploadProject(
            Long userId,
            MultipartFile file,
            ProjectUploadWebRequest webRequest
    ) {
        ProjectUploadRequest requestDto = projectWebMapper.toApplicationDto(webRequest);
        projectUploadUseCase.upload(userId, file, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(ProjectSuccessStatus.CREATED_PROJECT));
    }

    /**
     * 실시간 프로젝트 검색을 수행하여 결과 목록을 반환합니다.
     *
     * @param keyword 검색할 키워드
     * @param size 반환할 결과의 최대 개수
     * @return 실시간 프로젝트 검색 결과 목록이 포함된 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<ProjectRealTimeSearchWebResponse>>> search(String keyword, int size) {
        List<ProjectRealTimeSearchResponse> responseDto = projectRealTimeSearchUseCase.search(keyword, size);
        List<ProjectRealTimeSearchWebResponse> webResponse = responseDto.stream()
                .map(projectSearchWebMapper::toWeb)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.FIND_REAL_TIME_PROJECTS, webResponse));
    }
}
