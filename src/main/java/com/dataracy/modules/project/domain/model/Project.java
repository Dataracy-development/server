package com.dataracy.modules.project.domain.model;

import com.dataracy.modules.project.domain.model.reference.AnalysisPurpose;
import com.dataracy.modules.project.domain.model.reference.DataSource;
import com.dataracy.modules.user.domain.model.reference.AuthorLevel;
import lombok.*;

/**
 * 프로젝트 도메인 모델
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Project {

    private Long id;
    private String title;
    private Long topicId;
    private Long userId;
    private AnalysisPurpose analysisPurpose;
    private DataSource dataSource;
    private AuthorLevel authorLevel;
    private Boolean isNew;
    private Project parentProject;
    private String content;

    public static Project toDomain(
            Long id,
            String title,
            Long topicId,
            Long userId,
            AnalysisPurpose analysisPurpose,
            DataSource dataSource,
            AuthorLevel authorLevel,
            Boolean isNew,
            Project parentProject,
            String content
    ) {
        return Project.builder()
                .id(id)
                .title(title)
                .topicId(topicId)
                .userId(userId)
                .analysisPurpose(analysisPurpose)
                .dataSource(dataSource)
                .authorLevel(authorLevel)
                .isNew(isNew)
                .parentProject(parentProject)
                .content(content)
                .build();
    }
}
