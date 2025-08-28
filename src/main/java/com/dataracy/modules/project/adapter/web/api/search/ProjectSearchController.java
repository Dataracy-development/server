package com.dataracy.modules.project.adapter.web.api.search;

import com.dataracy.modules.common.dto.response.SuccessResponse;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.adapter.web.mapper.search.ProjectFilterWebMapper;
import com.dataracy.modules.project.adapter.web.mapper.search.ProjectSearchWebMapper;
import com.dataracy.modules.project.adapter.web.request.search.FilteringProjectWebRequest;
import com.dataracy.modules.project.adapter.web.response.search.FilteredProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.RealTimeProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.search.SimilarProjectWebResponse;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.RealTimeProjectResponse;
import com.dataracy.modules.project.application.dto.response.search.SimilarProjectResponse;
import com.dataracy.modules.project.application.port.in.query.search.SearchFilteredProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.search.SearchRealTimeProjectsUseCase;
import com.dataracy.modules.project.application.port.in.query.search.SearchSimilarProjectsUseCase;
import com.dataracy.modules.project.domain.status.ProjectSuccessStatus;
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
public class ProjectSearchController implements ProjectSearchApi {
    private final ProjectSearchWebMapper projectSearchWebMapper;
    private final ProjectFilterWebMapper projectFilterWebMapper;

    private final SearchRealTimeProjectsUseCase searchRealTimeProjectsUseCase;
    private final SearchSimilarProjectsUseCase searchSimilarProjectsUseCase;
    private final SearchFilteredProjectsUseCase searchFilteredProjectsUsecase;

    /**
     * 주어진 키워드로 실시간 프로젝트를 검색하여 최대 지정된 개수만큼의 결과 목록을 반환합니다.
     *
     * @param keyword 검색에 사용할 키워드
     * @param size 반환할 최대 결과 수
     * @return 실시간 프로젝트 웹 응답 목록이 포함된 성공 응답 객체
     */
    @Override
    public ResponseEntity<SuccessResponse<List<RealTimeProjectWebResponse>>> searchRealTimeProjects(String keyword, int size) {
        Instant startTime = LoggerFactory.api().logRequest("[SearchRealTimeProjects] 자동완성을 위한 실시간 프로젝트 목록 조회 API 요청 시작");
        List<RealTimeProjectWebResponse> webResponse;

        try {
            List<RealTimeProjectResponse> responseDto = searchRealTimeProjectsUseCase.searchByKeyword(keyword, size);
            webResponse = responseDto.stream()
                    .map(projectSearchWebMapper::toWebDto)
                    .toList();
        } finally {
            LoggerFactory.api().logResponse("[SearchRealTimeProjects] 자동완성을 위한 실시간 프로젝트 목록 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.FIND_REAL_TIME_PROJECTS, webResponse));
    }

    /**
     * 지정한 프로젝트 ID를 기준으로 유사한 프로젝트 목록을 조회합니다.
     *
     * @param projectId 유사 프로젝트를 찾을 기준이 되는 프로젝트의 ID
     * @param size 반환할 유사 프로젝트의 최대 개수
     * @return 유사 프로젝트 목록과 성공 상태가 포함된 HTTP 200 OK 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<List<SimilarProjectWebResponse>>> searchSimilarProjects(Long projectId, int size){
        Instant startTime = LoggerFactory.api().logRequest("[SearchSimilarProjects] 유사 프로젝트 목록 조회 API 요청 시작");
        List<SimilarProjectWebResponse> webResponse;

        try {
            List<SimilarProjectResponse> responseDto = searchSimilarProjectsUseCase.searchSimilarProjects(projectId, size);
            webResponse = responseDto.stream()
                    .map(projectSearchWebMapper::toWebDto)
                    .toList();
        } finally {
            LoggerFactory.api().logResponse("[SearchSimilarProjects] 유사 프로젝트 목록 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.FIND_SIMILAR_PROJECTS, webResponse));
    }

    /**
     * 필터 조건과 페이지네이션 정보를 이용해 프로젝트 목록을 검색합니다.
     *
     * @param webRequest 프로젝트 필터링 조건이 담긴 요청 객체
     * @param pageable 페이지네이션 정보
     * @return 필터링된 프로젝트 목록과 성공 상태가 포함된 응답
     */
    @Override
    public ResponseEntity<SuccessResponse<Page<FilteredProjectWebResponse>>> searchFilteredProjects(FilteringProjectWebRequest webRequest, Pageable pageable) {
        Instant startTime = LoggerFactory.api().logRequest("[SearchFilteredProjects] 필터링된 프로젝트 목록 조회 API 요청 시작");
        Page<FilteredProjectWebResponse> webResponse;

        try {
            FilteringProjectRequest requestDto = projectFilterWebMapper.toApplicationDto(webRequest);
            Page<FilteredProjectResponse> responseDto = searchFilteredProjectsUsecase.searchByFilters(requestDto, pageable);
            webResponse = responseDto.map(projectFilterWebMapper::toWebDto);
        } finally {
            LoggerFactory.api().logResponse("[SearchFilteredProjects] 필터링된 프로젝트 목록 조회 API 응답 완료", startTime);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(ProjectSuccessStatus.FIND_FILTERED_PROJECTS, webResponse));
    }
}
