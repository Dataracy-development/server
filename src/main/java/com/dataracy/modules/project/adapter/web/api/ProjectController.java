package com.dataracy.modules.project.adapter.web.api;

import com.dataracy.modules.auth.application.port.in.jwt.JwtValidateUseCase;
import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.support.annotation.AuthorizationProjectEdit;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import com.dataracy.modules.project.adapter.web.mapper.ProjectFilterWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.ProjectSearchWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.ProjectWebMapper;
import com.dataracy.modules.project.adapter.web.request.ProjectFilterWebRequest;
import com.dataracy.modules.project.adapter.web.request.ProjectModifyWebRequest;
import com.dataracy.modules.project.adapter.web.request.ProjectUploadWebRequest;
import com.dataracy.modules.project.adapter.web.response.*;
import com.dataracy.modules.project.application.dto.request.ProjectFilterRequest;
import com.dataracy.modules.project.application.dto.request.ProjectModifyRequest;
import com.dataracy.modules.project.application.dto.request.ProjectUploadRequest;
import com.dataracy.modules.project.application.dto.response.*;
import com.dataracy.modules.project.application.port.in.*;
import com.dataracy.modules.project.domain.status.ProjectSuccessStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProjectController implements ProjectApi {
    private final ExtractHeaderUtil extractHeaderUtil;

    private final ProjectWebMapper projectWebMapper;
    private final ProjectSearchWebMapper projectSearchWebMapper;
    private final ProjectFilterWebMapper projectFilterWebMapper;

    private final ProjectUploadUseCase projectUploadUseCase;
    private final ProjectRealTimeSearchUseCase projectRealTimeSearchUseCase;
    private final ProjectSimilarSearchUseCase projectSimilarSearchUseCase;
    private final ProjectPopularSearchUseCase projectPopularSearchUseCase;
    private final ProjectFilteredSearchUseCase projectFilteredSearchUsecase;
    private final ProjectDetailUseCase projectDetailUseCase;
    private final ContinueProjectUseCase continueProjectUseCase;
    private final ConnectedProjectAssociatedWithDataUseCase connectedProjectAssociatedWithDataUseCase;
    private final ProjectModifyUseCase projectModifyUseCase;
    private final ProjectDeleteUseCase projectDeleteUseCase;
    private final ProjectRestoreUseCase projectRestoreUseCase;
    private final JwtValidateUseCase jwtValidateUseCase;

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
     * @param projectId 유사 프로젝트를 찾을 기준 프로젝트의 ID
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

    /**
     * 지정한 개수만큼 인기 프로젝트 목록을 조회하여 반환합니다.
     *
     * @param size 조회할 인기 프로젝트의 최대 개수
     * @return 인기 프로젝트 목록과 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<ProjectPopularSearchWebResponse>>> searchPopularProjects(int size) {
        List<ProjectPopularSearchResponse> responseDto = projectPopularSearchUseCase.findPopularProjects(size);
        List<ProjectPopularSearchWebResponse> webResponse = responseDto.stream()
                .map(projectSearchWebMapper::toWeb)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.FIND_POPULAR_PROJECTS, webResponse));
    }

    /**
     * 필터 조건과 페이지네이션 정보를 이용해 프로젝트 목록을 검색합니다.
     *
     * @param webRequest 프로젝트 필터링 조건이 담긴 요청 객체
     * @param pageable 페이지네이션 정보
     * @return 필터링된 프로젝트 목록이 포함된 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<ProjectFilterWebResponse>>> searchFilteredProjects(ProjectFilterWebRequest webRequest, Pageable pageable) {
        ProjectFilterRequest requestDto = projectFilterWebMapper.toApplicationDto(webRequest);
        Page<ProjectFilterResponse> responseDto = projectFilteredSearchUsecase.findFilterdProjects(requestDto, pageable);
        Page<ProjectFilterWebResponse> webResponse = responseDto.map(projectFilterWebMapper::toWebDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.FIND_FILTERED_PROJECTS, webResponse));
    }

    /****
     * HTTP 요청에서 인증된 사용자 ID와 뷰어 ID를 추출하여, 지정된 프로젝트의 상세 정보를 반환합니다.
     *
     * @param request  인증 및 뷰어 식별을 위한 HTTP 요청 객체
     * @param response 뷰어 ID 추출을 위한 HTTP 응답 객체
     * @param projectId 상세 정보를 조회할 프로젝트의 ID
     * @return 프로젝트 상세 정보를 포함한 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<ProjectDetailWebResponse>> getProjectDetail(HttpServletRequest request, HttpServletResponse response, Long projectId) {
        Long userId = extractHeaderUtil.extractAuthenticatedUserIdFromRequest(request);
        String viewerId = extractHeaderUtil.extractViewerIdFromRequest(request, response);

        ProjectDetailResponse responseDto = projectDetailUseCase.getProjectDetail(projectId, userId, viewerId);
        ProjectDetailWebResponse webResponse = projectWebMapper.toWebDto(responseDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_PROJECT_DETAIL, webResponse));
    }

    /**
     * 지정한 프로젝트 ID를 기준으로 이어지는 프로젝트 목록을 페이지네이션하여 조회합니다.
     *
     * @param projectId 기준이 되는 프로젝트의 ID
     * @param pageable 페이지네이션 정보
     * @return 이어지는 프로젝트 목록과 함께 성공 응답을 반환합니다.
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<ContinueProjectWebResponse>>> searchContinueProjects(Long projectId, Pageable pageable) {
        Page<ContinueProjectResponse> responseDto = continueProjectUseCase.findContinueProjects(projectId, pageable);
        Page<ContinueProjectWebResponse> webResponse = responseDto.map(projectWebMapper::toWebDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_CONTINUE_PROJECTS, webResponse));
    }

    /**
     * 지정된 데이터 ID와 연결된 프로젝트 목록을 페이지네이션하여 조회합니다.
     *
     * @param dataId 연결된 데이터를 식별하는 ID
     * @param pageable 페이지네이션 정보
     * @return 연결된 프로젝트 목록을 포함한 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<ConnectedProjectAssociatedWithDataWebResponse>>> searchConnectedProjectsAssociatedWithData(Long dataId, Pageable pageable) {
        Page<ConnectedProjectAssociatedWithDataResponse> responseDto = connectedProjectAssociatedWithDataUseCase.findConnectedProjects(dataId, pageable);
        Page<ConnectedProjectAssociatedWithDataWebResponse> webResponse = responseDto.map(projectWebMapper::toWebDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_CONNECTED_PROJECTS_ASSOCIATED_DATA, webResponse));
    }

    /**
     * 프로젝트 정보를 수정한다.
     *
     * @param projectId 수정할 프로젝트의 ID
     * @param file      프로젝트 썸네일 파일 (선택 사항)
     * @param webRequest 프로젝트 수정 요청 정보
     * @return 수정 성공 여부를 나타내는 응답
     */
    @Override
    @AuthorizationProjectEdit
    public ResponseEntity<SuccessResponse<Void>> modifyProject(Long projectId, MultipartFile file, ProjectModifyWebRequest webRequest) {
        ProjectModifyRequest requestDto = projectWebMapper.toApplicationDto(webRequest);
        projectModifyUseCase.modify(projectId, file, requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.MODIFY_PROJECT));
    }

    /**
     * 지정된 프로젝트를 삭제 상태로 변경합니다.
     *
     * @param projectId 삭제할 프로젝트의 ID
     * @return 삭제 성공 여부를 나타내는 성공 응답
     */
    @Override
    @AuthorizationProjectEdit
    public ResponseEntity<SuccessResponse<Void>> deleteProject(Long projectId) {
        projectDeleteUseCase.markAsDelete(projectId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.DELETE_PROJECT));
    }

    /**
     * 삭제된 프로젝트를 복구합니다.
     *
     * @param projectId 복구할 프로젝트의 ID
     * @return 복구 성공 상태를 포함한 HTTP 200 OK 응답
     */
    @Override
    @AuthorizationProjectEdit(restore = true)
    public ResponseEntity<SuccessResponse<Void>> restoreProject(Long projectId) {
        projectRestoreUseCase.markAsRestore(projectId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.RESTORE_PROJECT));
    }
}
