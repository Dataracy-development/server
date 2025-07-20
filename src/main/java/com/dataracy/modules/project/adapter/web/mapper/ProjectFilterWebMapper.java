package com.dataracy.modules.project.adapter.web.mapper;

import com.dataracy.modules.project.adapter.web.request.ProjectFilterWebRequest;
import com.dataracy.modules.project.adapter.web.response.ChildProjectWebResponse;
import com.dataracy.modules.project.adapter.web.response.ProjectFilterWebResponse;
import com.dataracy.modules.project.application.dto.request.ProjectFilterRequest;
import com.dataracy.modules.project.application.dto.response.ProjectFilterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectFilterWebMapper {

    private final ChildProjectWebMapper childProjectWebMapper;

    /**
     * 웹 요청 DTO를 애플리케이션 계층의 프로젝트 필터 요청 DTO로 변환합니다.
     *
     * @param webRequest 웹 계층에서 전달된 프로젝트 필터 요청 DTO
     * @return 변환된 애플리케이션 계층의 프로젝트 필터 요청 DTO
     */
    public ProjectFilterRequest toApplicationDto(ProjectFilterWebRequest webRequest) {
        return new ProjectFilterRequest(
                webRequest.keyword(),
                webRequest.sortType(),
                webRequest.topicId(),
                webRequest.analysisPurposeId(),
                webRequest.dataSourceId(),
                webRequest.authorLevelId()
        );
    }

    /**
     * 애플리케이션 계층의 프로젝트 필터 응답 DTO를 웹 계층의 응답 DTO로 변환합니다.
     *
     * @param responseDto 애플리케이션 계층의 프로젝트 필터 응답 DTO
     * @return 웹 계층의 프로젝트 필터 응답 DTO
     */
    public ProjectFilterWebResponse toWebDto(ProjectFilterResponse responseDto) {

        List<ChildProjectWebResponse> childProjectWebResponses = responseDto.childProjects().stream()
                .map(childProjectWebMapper::toWebDto)
                .toList();

        return new ProjectFilterWebResponse(
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
