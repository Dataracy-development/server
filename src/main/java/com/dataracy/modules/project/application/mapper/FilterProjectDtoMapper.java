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
    /**
     * Project 도메인 객체와 관련 메타데이터를 ProjectFilterResponse DTO로 변환합니다.
     *
     * 프로젝트의 자식 프로젝트 목록을 ChildProjectResponse 리스트로 매핑하며, 자식 프로젝트의 작성자 이름이 제공되지 않은 경우 "익명 유저"로 대체합니다.
     *
     * @param project 변환할 프로젝트 도메인 객체
     * @param username 프로젝트 작성자 이름
     * @param topicLabel 주제 라벨
     * @param analysisPurposeLabel 분석 목적 라벨
     * @param dataSourceLabel 데이터 소스 라벨
     * @param authorLevelLabel 작성자 등급 라벨
     * @param childUsernames 자식 프로젝트의 userId와 username 매핑 정보
     * @return 변환된 ProjectFilterResponse DTO
     */
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
                        childUsernames.getOrDefault(child.getUserId(), "익명 유저"),
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
