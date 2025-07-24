package com.dataracy.modules.project.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import com.dataracy.modules.project.application.dto.request.ProjectModifyRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.LinkedHashSet;
import java.util.Set;

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
@SQLDelete(sql = "UPDATE project SET is_deleted = true WHERE project_id = ?")
@Where(clause = "is_deleted = false")
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
    private Set<ProjectEntity> childProjects = new LinkedHashSet<>();

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column
    private String fileUrl;

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    @Builder.Default
    private Set<ProjectDataEntity> projectDataEntities = new LinkedHashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Long commentCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    public void modify(ProjectModifyRequest requestDto, ProjectEntity parentProject) {
        this.title = requestDto.title();
        this.topicId = requestDto.topicId();
        this.analysisPurposeId = requestDto.analysisPurposeId();
        this.dataSourceId = requestDto.dataSourceId();
        this.authorLevelId = requestDto.authorLevelId();
        this.isContinue = requestDto.isContinue();
        this.parentProject = parentProject;
        this.content = requestDto.content();
    }

    /**
     * 프로젝트에 ProjectDataEntity를 추가하고 해당 데이터 엔티티의 프로젝트 참조를 이 프로젝트로 설정합니다.
     *
     * @param dataEntity 추가할 ProjectDataEntity 인스턴스
     */
    public void addProjectData(ProjectDataEntity dataEntity) {
        projectDataEntities.add(dataEntity);
        dataEntity.assignProject(this);
    }

    /**
     * 프로젝트의 파일 URL을 지정된 값으로 업데이트합니다.
     *
     * @param fileUrl 새로 설정할 파일 URL
     */
    public void updateFile(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    /**
     * 댓글 수를 1 증가시킵니다.
     */
    public void increaseCommentCount() {
        this.commentCount++;
    }
    /**
     * 댓글 수를 1 감소시킵니다. 최소값은 0입니다.
     */
    public void decreaseCommentCount() {
        this.commentCount = Math.max(0, this.commentCount - 1);
    }
    /**
     * 좋아요 수를 1 증가시킵니다.
     */
    public void increaseLikeCount() {
        this.likeCount++;
    }
    /**
     * 좋아요 수를 1 감소시킵니다. 값이 0보다 작아지지 않도록 보장합니다.
     */
    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }
    /**
     * 프로젝트의 조회수를 1 증가시킵니다.
     */
    public void increaseViewCount() {
        this.viewCount++;
    }
    /**
     * 조회수(viewCount)를 1 감소시킵니다. 값이 0보다 작아지지 않도록 보장합니다.
     */
    public void decreaseViewCount() {
        this.viewCount = Math.max(0, this.viewCount - 1);
    }


    /**
     * 주어진 정보로 새로운 ProjectEntity 인스턴스를 생성합니다.
     *
     * @param title 프로젝트의 제목
     * @param topicId 프로젝트가 속한 주제의 식별자
     * @param userId 프로젝트 작성자의 식별자
     * @param analysisPurposeId 분석 목적의 식별자
     * @param dataSourceId 데이터 소스의 식별자
     * @param authorLevelId 작성자 등급의 식별자
     * @param isContinue 프로젝트의 연속 여부
     * @param parentProject 상위 프로젝트 엔티티, 없으면 null
     * @param content 프로젝트의 상세 내용
     * @param fileUrl 첨부 파일의 URL, 없으면 null
     * @return 생성된 ProjectEntity 객체
     */
    public static ProjectEntity of(
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
