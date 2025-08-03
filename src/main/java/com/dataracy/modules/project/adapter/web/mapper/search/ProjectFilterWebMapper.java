package com.dataracy.modules.project.adapter.web.mapper.search;

import com.dataracy.modules.project.adapter.web.mapper.support.ChildProjectWebMapper;
import com.dataracy.modules.project.adapter.web.request.search.FilteringProjectWebRequest;
import com.dataracy.modules.project.adapter.web.response.search.FilteredProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.support.ChildProjectWebResponse;
import com.dataracy.modules.project.application.dto.request.search.FilteringProjectRequest;
import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectFilterWebMapper {

    private final ChildProjectWebMapper childProjectWebMapper;

    /**
     * 웹 계층의 프로젝트 필터링 요청 DTO를 애플리케이션 계층의 필터링 요청 DTO로 변환합니다.
     *
     * @param webRequest 필터링 조건이 포함된 웹 요청 DTO
     * @return 변환된 애플리케이션 계층의 필터링 요청 DTO
     */
    public FilteringProjectRequest toApplicationDto(FilteringProjectWebRequest webRequest) {
        return new FilteringProjectRequest(
                webRequest.keyword(),
                webRequest.sortType(),
                webRequest.topicId(),
                webRequest.analysisPurposeId(),
                webRequest.dataSourceId(),
                webRequest.authorLevelId()
        );
    }

    /**
     * 애플리케이션 계층의 필터링된 프로젝트 응답 DTO를 웹 계층의 응답 DTO로 변환합니다.
     *
     * @param responseDto 필터링된 프로젝트에 대한 애플리케이션 계층 응답 DTO
     * @return 웹 계층에서 사용하는 필터링된 프로젝트 응답 DTO
     */
    public FilteredProjectWebResponse toWebDto(FilteredProjectResponse responseDto) {

        List<ChildProjectWebResponse> childProjectWebResponses = responseDto.childProjects().stream()
                .map(childProjectWebMapper::toWebDto)
                .toList();

        return new FilteredProjectWebResponse(
                responseDto.id(),
                responseDto.title(),
                responseDto.content(),
                responseDto.username(),
                responseDto.fileUrl(),
                responseDto.topicLabel(),
                responseDto.analysisPurposeLabel(),
                responseDto.dataSourceLabel(),
                responseDto.authorLevelLabel(),
                responseDto.commentCount(),
                responseDto.likeCount(),
                responseDto.viewCount(),
                responseDto.createdAt(),
                childProjectWebResponses
        );
    }
}
