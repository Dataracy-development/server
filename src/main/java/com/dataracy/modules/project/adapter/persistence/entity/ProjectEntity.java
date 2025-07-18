package com.dataracy.modules.project.adapter.persistence.entity;

import com.dataracy.modules.common.base.BaseEntity;
import com.dataracy.modules.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

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
public class ProjectEntity extends BaseTimeEntity {
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

    @Column(nullable = false)
    private Long analysisPurposeId;

    @Column(nullable = false)
    private Long dataSourceId;

    @Column(nullable = false)
    private Long authorLevelId;

    @Column(nullable = false)
    private Boolean isContinue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_project_id")
    private ProjectEntity parentProject;

    @OneToMany(mappedBy = "parentProject", cascade = CascadeType.PERSIST)
    @BatchSize(size = 10)
    @Builder.Default
    private List<ProjectEntity> childProjects = new ArrayList<>();

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column
    private String fileUrl;

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<ProjectDataEntity> projectDataEntities = new ArrayList<>();

    // 프로젝트에 데이터셋을 추가한다
    public void addProjectData(ProjectDataEntity dataEntity) {
        projectDataEntities.add(dataEntity);
        dataEntity.assignProject(this);
    }

    /**
     * 프로젝트의 파일 URL을 새로운 값으로 변경합니다.
     *
     * @param fileUrl 새로 지정할 파일의 URL
     */
    public void updateFile (String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 주어진 값들로 새로운 ProjectEntity 인스턴스를 생성합니다.
     *
     * @param title 프로젝트 제목
     * @param topicId 주제 식별자
     * @param userId 사용자 식별자
     * @param analysisPurposeId 분석 목적 식별자
     * @param dataSourceId 데이터 소스 식별자
     * @param authorLevelId 작성자 등급 식별자
     * @param isContinue 프로젝트의 연속 여부
     * @param parentProject 상위 프로젝트 엔티티, 없으면 null
     * @param content 프로젝트 상세 내용
     * @param fileUrl 파일의 URL, 없으면 null
     * @return 생성된 ProjectEntity 객체
     */
    public static ProjectEntity toEntity(
            String title,
            Long topicId,
            Long userId,
            Long analysisPurposeId,
            Long dataSourceId,
            Long authorLevelId,
            Boolean isContinue,
            ProjectEntity parentProject,
            String content,
            String fileUrl
    ) {
        return ProjectEntity.builder()
                .title(title)
                .topicId(topicId)
                .userId(userId)
                .analysisPurposeId(analysisPurposeId)
                .dataSourceId(dataSourceId)
                .authorLevelId(authorLevelId)
                .isContinue(isContinue)
                .parentProject(parentProject)
                .content(content)
                .fileUrl(fileUrl)
                .build();
    }
}
