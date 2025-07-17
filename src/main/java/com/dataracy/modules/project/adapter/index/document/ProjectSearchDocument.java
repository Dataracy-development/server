package com.dataracy.modules.project.adapter.index.document;

import com.dataracy.modules.project.domain.model.Project;
import lombok.Builder;

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
        String fileUrl
) {
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
                .build();
    }
}
