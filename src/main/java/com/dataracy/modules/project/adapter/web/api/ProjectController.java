package com.dataracy.modules.project.adapter.web.api;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.project.adapter.web.mapper.ProjectSearchWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.ProjectWebMapper;
import com.dataracy.modules.project.adapter.web.request.ProjectUploadWebRequest;
import com.dataracy.modules.project.adapter.web.response.ProjectPopularSearchWebResponse;
import com.dataracy.modules.project.adapter.web.response.ProjectRealTimeSearchWebResponse;
import com.dataracy.modules.project.adapter.web.response.ProjectSimilarSearchWebResponse;
import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import com.dataracy.modules.project.application.dto.response.ProjectPopularSearchResponse;
import com.dataracy.modules.project.application.dto.response.ProjectRealTimeSearchResponse;
import com.dataracy.modules.project.application.dto.response.ProjectSimilarSearchResponse;
import com.dataracy.modules.project.application.port.in.ProjectPopularSearchUseCase;
import com.dataracy.modules.project.application.port.in.ProjectRealTimeSearchUseCase;
import com.dataracy.modules.project.application.port.in.ProjectSimilarSearchUseCase;
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
    private final ProjectSimilarSearchUseCase projectSimilarSearchUseCase;
    private final ProjectPopularSearchUseCase projectPopularSearchUseCase;

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
     * 주어진 키워드로 실시간 프로젝트를 검색하여 결과 목록을 반환합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 최대 결과 수
     * @return 실시간 프로젝트 검색 결과 목록이 포함된 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<ProjectRealTimeSearchWebResponse>>> searchRealTimeProjects(String keyword, int size) {
        List<ProjectRealTimeSearchResponse> responseDto = projectRealTimeSearchUseCase.searchByKeyword(keyword, size);
        List<ProjectRealTimeSearchWebResponse> webResponse = responseDto.stream()
                .map(projectSearchWebMapper::toWeb)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.FIND_REAL_TIME_PROJECTS, webResponse));
    }

    /**
     * 지정한 프로젝트와 유사한 프로젝트 목록을 조회하여 반환합니다.
     *
     * @param projectId 기준이 되는 프로젝트의 ID
     * @param size 반환할 유사 프로젝트의 최대 개수
     * @return 유사 프로젝트 목록과 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<ProjectSimilarSearchWebResponse>>> searchSimilarProjects(Long projectId, int size){
        List<ProjectSimilarSearchResponse> responseDto = projectSimilarSearchUseCase.findSimilarProjects(projectId, size);
        List<ProjectSimilarSearchWebResponse> webResponse = responseDto.stream()
                .map(projectSearchWebMapper::toWeb)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.FIND_SIMILAR_PROJECTS, webResponse));
    }

    @Override
    public ResponseEntity<SuccessResponse<List<ProjectPopularSearchWebResponse>>> searchPopularProjects(int size) {
        List<ProjectPopularSearchResponse> responseDto = projectPopularSearchUseCase.findPopularProjects(size);
        List<ProjectPopularSearchWebResponse> webResponse = responseDto.stream()
                .map(projectSearchWebMapper::toWeb)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.FIND_POPULAR_PROJECTS, webResponse));
    }
}
