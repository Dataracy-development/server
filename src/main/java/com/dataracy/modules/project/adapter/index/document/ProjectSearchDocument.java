package com.dataracy.modules.project.adapter.index.document;

import com.dataracy.modules.project.domain.model.Project;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProjectSearchDocument(
        Long id,
        String title,
        String content,
        Long topicId,
        Long userId,
        String username,
        Long analysisPurposeId,
        Long dataSourceId,
        Long authorLevelId,
        Boolean isContinue,
        String fileUrl,
        LocalDateTime createdAt
) {
    /**
     * 주어진 Project 객체와 사용자 이름을 기반으로 ProjectSearchDocument 인스턴스를 생성합니다.
     *
     * @param project 프로젝트 도메인 객체
     * @param username 프로젝트와 연관된 사용자 이름
     * @return ProjectSearchDocument로 변환된 인스턴스
     */
    public static ProjectSearchDocument from(Project project, String username) {
        return ProjectSearchDocument.builder()
                .id(project.getId())
                .title(project.getTitle())
                .content(project.getContent())
                .topicId(project.getTopicId())
                .userId(project.getUserId())
                .username(username)
                .analysisPurposeId(project.getAnalysisPurposeId())
                .dataSourceId(project.getDataSourceId())
                .authorLevelId(project.getAuthorLevelId())
                .isContinue(project.getIsContinue())
                .fileUrl(project.getFileUrl())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
