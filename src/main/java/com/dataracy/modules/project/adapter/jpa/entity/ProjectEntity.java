package com.dataracy.modules.project.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.project.application.dto.request.command.ModifyProjectRequest;
import com.dataracy.modules.project.domain.exception.ProjectException;
import com.dataracy.modules.project.domain.status.ProjectErrorStatus;
import jakarta.persistence.*;
import lombok.*;
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
@Where(clause = "is_deleted = false")
public class ProjectEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long userId;

    // 도메인 : 타 어그리거트이므로 id로 간접참조
    @Column(nullable = false)
    private Long analysisPurposeId;

    @Column(nullable = false)
    private Long dataSourceId;

    @Column(nullable = false)
    private Long authorLevelId;

    @Column(nullable = false)
    private Long topicId;

    @Column(nullable = false)
    private Boolean isContinue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_project_id")
    private ProjectEntity parentProject;

    @OneToMany(mappedBy = "parentProject", cascade = CascadeType.PERSIST)
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

    /**
     * 프로젝트의 주요 정보를 수정 요청 DTO와 새로운 부모 프로젝트로 갱신합니다.
     *
     * @param requestDto 프로젝트의 수정 정보를 포함한 DTO
     * @param parentProject 새롭게 지정할 부모 프로젝트 엔티티
     */
    public void modify(ModifyProjectRequest requestDto, ProjectEntity parentProject) {
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
     * 프로젝트에 연관된 ProjectDataEntity를 추가하고, 해당 데이터 엔티티의 프로젝트 참조를 현재 프로젝트로 설정합니다.
     *
     * @param dataEntity 추가할 프로젝트 데이터 엔티티
     */
    public void addProjectData(ProjectDataEntity dataEntity) {
        projectDataEntities.add(dataEntity);
        dataEntity.assignProject(this);
    }

    /**
     * 부모 프로젝트 참조를 제거합니다.
     */
    public void deleteParentProject() {
        this.parentProject = null;
    }

    /****
     * 프로젝트의 파일 URL을 유효성 검사 후 새로운 값으로 변경합니다.
     *
     * @param fileUrl 변경할 파일 URL. null이거나 비어 있으면 예외가 발생합니다.
     * @throws ProjectException 파일 URL이 null이거나 비어 있을 때 발생합니다.
     */
    public void updateFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            LoggerFactory.domain().logWarning("잘못된 프로젝트 파일 url 형식입니다.");
            throw new ProjectException(ProjectErrorStatus.INVALID_FILE_URL);
        }
        if (fileUrl.equals(this.fileUrl)) {
            return;
        }
        this.fileUrl = fileUrl;
    }

    /**
     * 프로젝트를 소프트 삭제 처리하여 삭제된 상태로 표시합니다.
     */
    public void delete() {
        this.isDeleted = true;
    }

    /**
     * 프로젝트를 복구하여 삭제 상태를 해제합니다.
     *
     * 이 메서드는 소프트 삭제된 프로젝트의 `isDeleted` 플래그를 `false`로 변경하여 프로젝트를 다시 활성화합니다.
     */
    public void restore() {
        this.isDeleted = false;
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
