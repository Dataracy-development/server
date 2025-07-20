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
