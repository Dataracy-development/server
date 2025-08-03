package com.dataracy.modules.project.adapter.web.api.read;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import com.dataracy.modules.project.adapter.web.mapper.read.ProjectReadWebMapper;
import com.dataracy.modules.project.adapter.web.response.read.ConnectedProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.ContinuedProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.read.ProjectDetailWebResponse;
import com.dataracy.modules.project.application.dto.response.read.ConnectedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ContinuedProjectResponse;
import com.dataracy.modules.project.application.dto.response.read.ProjectDetailResponse;
import com.dataracy.modules.project.application.port.in.query.read.FindConnectedProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.FindContinuedProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.read.GetProjectDetailUseCase;
import com.dataracy.modules.project.domain.status.ProjectSuccessStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class ProjectReadController implements ProjectReadApi {
    private final ExtractHeaderUtil extractHeaderUtil;

    private final ProjectReadWebMapper projectReadWebMapper;

    private final GetProjectDetailUseCase getProjectDetailUseCase;
    private final FindContinuedProjectsUseCase findContinuedProjectsUseCase;
    private final FindConnectedProjectsUseCase findConnectedProjectsUseCase;

    /****
     * 지정된 프로젝트의 상세 정보를 조회하여 성공 응답으로 반환합니다.
     *
     * HTTP 요청 및 응답에서 인증된 사용자 ID와 뷰어 ID를 추출한 뒤, 해당 정보를 기반으로 프로젝트 상세 정보를 조회합니다.
     *
     * @param request 인증 및 뷰어 식별을 위한 HTTP 요청 객체
     * @param response 뷰어 ID 추출을 위한 HTTP 응답 객체
     * @param projectId 상세 정보를 조회할 프로젝트의 ID
     * @return 프로젝트 상세 정보를 포함한 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<ProjectDetailWebResponse>> getProjectDetail(HttpServletRequest request, HttpServletResponse response, Long projectId) {
        Instant startTime = LoggerFactory.api().logRequest("[GetProjectDetail] 프로젝트 상세 정보 조회 API 요청 시작");

        Long userId = extractHeaderUtil.extractAuthenticatedUserIdFromRequest(request);
        String viewerId = extractHeaderUtil.extractViewerIdFromRequest(request, response);

        ProjectDetailResponse responseDto = getProjectDetailUseCase.getProjectDetail(projectId, userId, viewerId);
        ProjectDetailWebResponse webResponse = projectReadWebMapper.toWebDto(responseDto);

        LoggerFactory.api().logResponse("[GetProjectDetail] 프로젝트 상세 정보 조회 API 응답 완료", startTime);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_PROJECT_DETAIL, webResponse));
    }

    /****
     * 지정한 프로젝트 ID를 기준으로 이어지는 프로젝트 목록을 페이지네이션하여 반환합니다.
     *
     * @param projectId 이어지는 프로젝트를 조회할 기준 프로젝트의 ID
     * @param pageable 결과 페이지네이션 정보
     * @return 이어지는 프로젝트 목록이 포함된 성공 응답 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<ContinuedProjectWebResponse>>> findContinueProjects(Long projectId, Pageable pageable) {
        Instant startTime = LoggerFactory.api().logRequest("[FindContinueProjects] 이어가기 프로젝트 목록 조회 API 요청 시작");

        Page<ContinuedProjectResponse> responseDto = findContinuedProjectsUseCase.findContinuedProjects(projectId, pageable);
        Page<ContinuedProjectWebResponse> webResponse = responseDto.map(projectReadWebMapper::toWebDto);

        LoggerFactory.api().logResponse("[FindContinueProjects] 이어가기 프로젝트 목록 조회 API 응답 완료", startTime);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_CONTINUE_PROJECTS, webResponse));
    }

    /**
     * 지정된 데이터 ID와 연결된 프로젝트 목록을 페이지네이션하여 반환합니다.
     *
     * @param dataId 연결된 데이터를 식별하는 고유 ID
     * @param pageable 결과 페이지네이션을 위한 정보
     * @return 연결된 프로젝트 목록이 포함된 성공 응답 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<ConnectedProjectWebResponse>>> findConnectedProjectsAssociatedWithData(Long dataId, Pageable pageable) {
        Instant startTime = LoggerFactory.api().logRequest("[FindConnectedProjectsAssociatedWithData] 데이터셋과 연결된 프로젝트 목록 조회 API 요청 시작");

        Page<ConnectedProjectResponse> responseDto = findConnectedProjectsUseCase.findConnectedProjects(dataId, pageable);
        Page<ConnectedProjectWebResponse> webResponse = responseDto.map(projectReadWebMapper::toWebDto);

        LoggerFactory.api().logResponse("[FindConnectedProjectsAssociatedWithData] 데이터셋과 연결된 프로젝트 목록 조회 API 응답 완료", startTime);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_CONNECTED_PROJECTS_ASSOCIATED_DATA, webResponse));
    }
}
