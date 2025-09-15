package com.dataracy.modules.project.adapter.web.api.read;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.common.util.ExtractHeaderUtil;
import com.dataracy.modules.project.adapter.web.mapper.read.ProjectReadWebMapper;
import com.dataracy.modules.project.adapter.web.response.read.*;
import com.dataracy.modules.project.application.dto.response.read.*;
import com.dataracy.modules.project.application.port.in.query.read.*;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectReadController implements ProjectReadApi {
    private final ExtractHeaderUtil extractHeaderUtil;

    private final ProjectReadWebMapper projectReadWebMapper;

    private final GetProjectDetailUseCase getProjectDetailUseCase;
    private final FindContinuedProjectsUseCase findContinuedProjectsUseCase;
    private final FindConnectedProjectsUseCase findConnectedProjectsUseCase;
    private final GetPopularProjectsUseCase getPopularProjectsUseCase;
    private final FindUserProjectsUseCase findUserProjectsUseCase;

    /**
     * 지정된 프로젝트의 상세 정보를 조회하여 성공 응답으로 반환합니다.
     *
     * HTTP 요청 및 응답에서 인증된 사용자 ID와 뷰어 ID를 추출한 후, 해당 정보를 기반으로 프로젝트의 상세 정보를 조회하여 반환합니다.
     *
     * @param request 인증 및 뷰어 식별을 위한 HTTP 요청 객체
     * @param response 뷰어 ID 추출을 위한 HTTP 응답 객체
     * @param projectId 상세 정보를 조회할 프로젝트의 ID
     * @return 프로젝트 상세 정보를 포함한 성공 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<ProjectDetailWebResponse>> getProjectDetail(HttpServletRequest request, HttpServletResponse response, Long projectId) {
        Instant startTime = LoggerFactory.api().logRequest("[GetProjectDetail] 프로젝트 상세 정보 조회 API 요청 시작");
        ProjectDetailWebResponse webResponse;

        try {
            Long userId = extractHeaderUtil.extractAuthenticatedUserIdFromRequest(request);
            String viewerId = extractHeaderUtil.extractViewerIdFromRequest(request, response);

            ProjectDetailResponse responseDto = getProjectDetailUseCase.getProjectDetail(projectId, userId, viewerId);
            webResponse = projectReadWebMapper.toWebDto(responseDto);
        } finally {
            LoggerFactory.api().logResponse("[GetProjectDetail] 프로젝트 상세 정보 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_PROJECT_DETAIL, webResponse));
    }

    /**
     * 기준 프로젝트 ID를 기반으로 이어지는 프로젝트 목록을 페이지네이션하여 조회합니다.
     *
     * @param projectId 이어지는 프로젝트를 조회할 기준이 되는 프로젝트의 ID
     * @param pageable 결과의 페이지네이션 정보를 담는 객체
     * @return 이어지는 프로젝트 목록이 포함된 성공 응답 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<ContinuedProjectWebResponse>>> findContinueProjects(Long projectId, Pageable pageable) {
        Instant startTime = LoggerFactory.api().logRequest("[FindContinueProjects] 이어가기 프로젝트 목록 조회 API 요청 시작");
        Page<ContinuedProjectWebResponse> webResponse;

        try {
            Page<ContinuedProjectResponse> responseDto = findContinuedProjectsUseCase.findContinuedProjects(projectId, pageable);
            webResponse = responseDto.map(projectReadWebMapper::toWebDto);
        } finally {
            LoggerFactory.api().logResponse("[FindContinueProjects] 이어가기 프로젝트 목록 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_CONTINUE_PROJECTS, webResponse));
    }

    /**
     * 지정된 데이터 ID와 연결된 프로젝트 목록을 페이지네이션하여 반환합니다.
     *
     * @param dataId 프로젝트와 연결된 데이터의 고유 ID
     * @param pageable 결과 페이지네이션을 위한 정보
     * @return 연결된 프로젝트 목록이 포함된 성공 응답 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<ConnectedProjectWebResponse>>> findConnectedProjectsAssociatedWithData(Long dataId, Pageable pageable) {
        Instant startTime = LoggerFactory.api().logRequest("[FindConnectedProjectsAssociatedWithData] 데이터셋과 연결된 프로젝트 목록 조회 API 요청 시작");
        Page<ConnectedProjectWebResponse> webResponse;

        try {
            Page<ConnectedProjectResponse> responseDto = findConnectedProjectsUseCase.findConnectedProjects(dataId, pageable);
            webResponse = responseDto.map(projectReadWebMapper::toWebDto);
        } finally {
            LoggerFactory.api().logResponse("[FindConnectedProjectsAssociatedWithData] 데이터셋과 연결된 프로젝트 목록 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_CONNECTED_PROJECTS_ASSOCIATED_DATA, webResponse));
    }

    /**
     * 요청된 개수만큼 인기 프로젝트 목록을 조회하여 성공 응답으로 반환합니다.
     *
     * @param size 반환할 인기 프로젝트의 최대 개수
     * @return 인기 프로젝트 목록과 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<PopularProjectWebResponse>>> getPopularProjects(int size) {
        Instant startTime = LoggerFactory.api().logRequest("[GetPopularProjects] 인기 프로젝트 목록 조회 API 요청 시작");
        List<PopularProjectWebResponse> webResponse;

        try {
            List<PopularProjectResponse> responseDto = getPopularProjectsUseCase.getPopularProjects(size);
            webResponse = responseDto.stream()
                    .map(projectReadWebMapper::toWebDto)
                    .toList();
        } finally {
            LoggerFactory.api().logResponse("[GetPopularProjects] 인기 프로젝트 목록 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.FIND_POPULAR_PROJECTS, webResponse));
    }

    /**
     * 지정한 사용자가 업로드한 프로젝트를 페이지 단위로 조회하여 반환합니다.
     *
     * 요청된 페이지 정보에 따라 FindUserProjectsUseCase로부터 프로젝트 목록을 조회하고,
     * 각 도메인 응답을 UserProjectWebResponse로 매핑한 결과를 SuccessResponse(GET_USER_PROJECTS)로 감싸
     * HTTP 200 응답으로 반환합니다.
     *
     * @param userId  조회 대상 사용자의 식별자
     * @param pageable  요청할 페이지 번호, 크기, 정렬 정보를 포함한 Pageable 객체
     * @return HTTP 200과 함께 페이지화된 UserProjectWebResponse를 담은 SuccessResponse
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<UserProjectWebResponse>>> findUserProjects(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.api().logRequest("[FindUserProjects] 로그인한 회원이 업로드한 프로젝트 리스트를 조회 API 요청 시작");
        Page<UserProjectWebResponse> webResponse;

        try {
            Page<UserProjectResponse> responseDto = findUserProjectsUseCase.findUserProjects(userId, pageable);
            webResponse = responseDto.map(projectReadWebMapper::toWebDto);
        } finally {
            LoggerFactory.api().logResponse("[FindUserProjects] 로그인한 회원이 업로드한 프로젝트 리스트를 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_USER_PROJECTS, webResponse));
    }

    /**
     * 지정한 사용자가 '좋아요'한 프로젝트들의 페이지를 조회하여 웹 응답 DTO로 반환한다.
     *
     * @param userId   좋아요 목록을 조회할 사용자의 ID
     * @param pageable 페이지 번호·크기·정렬을 포함한 페이징 정보
     * @return 해당 사용자가 좋아요한 프로젝트들의 페이지를 담은 SuccessResponse (HTTP 200)
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<UserProjectWebResponse>>> findLikeProjects(Long userId, Pageable pageable) {
        Instant startTime = LoggerFactory.api().logRequest("[FindLikeProjects] 로그인한 회원이 좋아요한 프로젝트 리스트를 조회 API 요청 시작");
        Page<UserProjectWebResponse> webResponse;

        try {
            Page<UserProjectResponse> responseDto = findUserProjectsUseCase.findLikeProjects(userId, pageable);
            webResponse = responseDto.map(projectReadWebMapper::toWebDto);
        } finally {
            LoggerFactory.api().logResponse("[FindLikeProjects] 로그인한 회원이 좋아요한 프로젝트 리스트를 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.GET_LIKE_PROJECTS, webResponse));

    }
}
