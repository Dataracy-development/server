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
        String topicLabel,
        Long userId,
        String username,
        Long analysisPurposeId,
        String analysisPurposeLabel,
        Long dataSourceId,
        String dataSourceLabel,
        Long authorLevelId,
        String authorLevelLabel,
        Boolean isContinue,
        String fileUrl,
        LocalDateTime createdAt
) {
    /**
     * Project 도메인 객체와 관련 라벨, 사용자 이름을 기반으로 ProjectSearchDocument 인스턴스를 생성합니다.
     *
     * @param project 변환할 프로젝트 도메인 객체
     * @param topicLabel 프로젝트의 주제 라벨
     * @param analysisPurposeLabel 분석 목적 라벨
     * @param dataSourceLabel 데이터 소스 라벨
     * @param authorLevelLabel 작성자 등급 라벨
     * @param username 프로젝트와 연관된 사용자 이름
     * @return 프로젝트 및 관련 정보가 포함된 ProjectSearchDocument 인스턴스
     */
    public static ProjectSearchDocument from(
            Project project,
            String topicLabel,
            String analysisPurposeLabel,
            String dataSourceLabel,
            String authorLevelLabel,
            String username
    ) {
        return ProjectSearchDocument.builder()
                .id(project.getId())
                .title(project.getTitle())
                .content(project.getContent())
                .topicId(project.getTopicId())
                .topicLabel(topicLabel)
                .userId(project.getUserId())
                .username(username)
                .analysisPurposeId(project.getAnalysisPurposeId())
                .analysisPurposeLabel(analysisPurposeLabel)
                .dataSourceId(project.getDataSourceId())
                .dataSourceLabel(dataSourceLabel)
                .authorLevelId(project.getAuthorLevelId())
                .authorLevelLabel(authorLevelLabel)
                .isContinue(project.getIsContinue())
                .fileUrl(project.getFileUrl())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
