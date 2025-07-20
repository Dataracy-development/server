package com.dataracy.modules.project.application.mapper;

import com.dataracy.modules.project.application.dto.response.ChildProjectResponse;
import com.dataracy.modules.project.application.dto.response.ProjectFilterResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 필터링된 프로젝트 도메인 DTO와 필터링된 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class FilterProjectDtoMapper {
    public ProjectFilterResponse toResponseDto(
            Project project,
            String username,
            String topicLabel,
            String analysisPurposeLabel,
            String dataSourceLabel,
            String authorLevelLabel,
            Map<Long, String> childUsernames
    ) {
        List<ChildProjectResponse> childProjects = project.getChildProjects().stream()
                .map(child -> new ChildProjectResponse(
                        child.getId(),
                        child.getTitle(),
                        child.getContent(),
                        childUsernames.get(child.getUserId()),
                        child.getCommentCount(),
                        child.getLikeCount()
                ))
                .toList();

        return new ProjectFilterResponse(
                project.getId(),
                project.getTitle(),
                project.getContent(),
                username,
                project.getFileUrl(),
                topicLabel,
                analysisPurposeLabel,
                dataSourceLabel,
                authorLevelLabel,
                project.getCommentCount(),
                project.getLikeCount(),
                project.getViewCount(),
                project.getCreatedAt(),
                childProjects
        );
    }
}
