package com.dataracy.modules.project.application.mapper.search;

import com.dataracy.modules.project.application.dto.response.search.FilteredProjectResponse;
import com.dataracy.modules.project.application.dto.response.support.ChildProjectResponse;
import com.dataracy.modules.project.domain.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 필터링된 프로젝트 도메인 DTO와 필터링된 프로젝트 도메인 모델을 변환하는 매퍼
 */
@Component
public class FilteredProjectDtoMapper {
    /**
     * Project 도메인과 관련 라벨들을 조합해 FilteredProjectResponse DTO를 생성합니다.
     *
     * 전달된 project의 자식 프로젝트들을 ChildProjectResponse로 매핑하며,
     * 자식의 작성자 이름은 childUsernames 맵에서 userId로 조회하고 없으면 "익명 유저"로 대체합니다.
     *
     * @param childUsernames 자식 프로젝트의 userId를 키로 하여 username을 담은 맵(없을 경우 해당 자식의 authorName은 "익명 유저"로 설정)
     * @return 변환된 FilteredProjectResponse DTO
     */
    public FilteredProjectResponse toResponseDto(
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
                        child.getUserId(),
                        childUsernames.getOrDefault(child.getUserId(), "익명 유저"),
                        child.getCommentCount(),
                        child.getLikeCount()
                ))
                .toList();

        return new FilteredProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getContent(),
                project.getUserId(),
                username,
                project.getThumbnailUrl(),
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
