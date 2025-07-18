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

    /**
     * 주어진 프로젝트와 데이터 ID를 연결하는 ProjectDataEntity 인스턴스를 생성합니다.
     *
     * @param project 연결할 ProjectEntity 객체
     * @param dataId 연결할 데이터의 식별자
     * @return 프로젝트와 데이터 간의 관계를 나타내는 ProjectDataEntity 인스턴스
     */
    public static ProjectDataEntity of(ProjectEntity project, Long dataId) {
        ProjectDataEntity projectDataEntity = ProjectDataEntity.builder()
                .dataId(dataId)
                .build();
        projectDataEntity.assignProject(project);
        return projectDataEntity;
    }

    /**
     * 주어진 프로젝트 엔티티를 이 엔티티에 할당합니다.
     *
     * @param project 할당할 프로젝트 엔티티
     */
    public void assignProject(ProjectEntity project) {
        this.project = project;
    }
}
