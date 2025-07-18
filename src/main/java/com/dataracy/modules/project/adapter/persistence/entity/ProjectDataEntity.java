package com.dataracy.modules.project.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * project, data 다대다 연결 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "project_data")
public class ProjectDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_data_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @Column(nullable = false)
    private Long dataId;

    public static ProjectDataEntity of(ProjectEntity project, Long dataId) {
        ProjectDataEntity projectDataEntity = ProjectDataEntity.builder()
                .dataId(dataId)
                .build();
        projectDataEntity.assignProject(project);
        return projectDataEntity;
    }

    public void assignProject(ProjectEntity project) {
        this.project = project;
    }
}
