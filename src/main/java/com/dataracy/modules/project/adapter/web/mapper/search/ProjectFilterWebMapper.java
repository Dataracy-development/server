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
     * 애플리케이션 계층의 FilteredProjectResponse를 웹 계층의 FilteredProjectWebResponse로 변환합니다.
     *
     * <p>응답의 기본 필드(id, title, content 등)와 생성자 정보(creatorId, creatorName)를 그대로 매핑하며,
     * 하위 프로젝트 목록은 ChildProjectWebMapper를 통해 ChildProjectWebResponse 목록으로 변환하여 포함합니다.</p>
     *
     * @param responseDto 변환할 애플리케이션 계층의 필터링된 프로젝트 응답 DTO
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
                responseDto.creatorId(),
                responseDto.creatorName(),
                responseDto.projectThumbnailUrl(),
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
