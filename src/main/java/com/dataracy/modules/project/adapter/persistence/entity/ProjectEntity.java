package com.dataracy.modules.project.adapter.persistence.entity;

import com.dataracy.modules.common.base.BaseEntity;
import com.dataracy.modules.reference.adapter.persistence.entity.AnalysisPurposeEntity;
import com.dataracy.modules.reference.adapter.persistence.entity.DataSourceEntity;
import com.dataracy.modules.reference.adapter.persistence.entity.AuthorLevelEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * project 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "project"
)
public class ProjectEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    // 도메인 : 타 어그리거트이므로 id로 간접참조
    @Column(nullable = false)
    private Long topicId;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_purpose_id", nullable = false)
    private AnalysisPurposeEntity analysisPurpose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id", nullable = false)
    private DataSourceEntity dataSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_level_id", nullable = false)
    private AuthorLevelEntity authorLevel;

    @Column(nullable = false)
    private Boolean isNew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_project_id")
    private ProjectEntity parentProject;

    @OneToMany(mappedBy = "parentProject")
    @Builder.Default
    private List<ProjectEntity> childProjects = new ArrayList<>();

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public static ProjectEntity toEntity(
            Long id,
            String title,
            Long topicId,
            Long userId,
            AnalysisPurposeEntity analysisPurpose,
            DataSourceEntity dataSource,
            AuthorLevelEntity authorLevel,
            Boolean isNew,
            ProjectEntity project,
            String content
    ) {
        return ProjectEntity.builder()
                .id(id)
                .title(title)
                .topicId(topicId)
                .userId(userId)
                .analysisPurpose(analysisPurpose)
                .dataSource(dataSource)
                .authorLevel(authorLevel)
                .isNew(isNew)
                .parentProject(project)
                .content(content)
                .build();
    }
}
