package com.dataracy.modules.project.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import com.dataracy.modules.project.domain.enums.ProjectEsProjectionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "project_es_projection_queue",
        indexes = {
                @Index(name = "idx_proj_esq_status_nextRunAt_id", columnList = "status,next_run_at,id"),
                @Index(name = "idx_proj_esq_projectId", columnList = "project_id")
        }
)
public class ProjectEsProjectionTaskEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 프로젝트 문서를 업데이트할지
    @Column(nullable = false)
    private Long projectId;

    // 증감치: 댓글/좋아요 둘 다 지원
    @Column(nullable = false)
    @Builder.Default
    private Integer deltaComment = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer deltaLike = 0;

    @Column(name = "set_deleted")
    private Boolean setDeleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectEsProjectionType status = ProjectEsProjectionType.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(nullable = false)
    private LocalDateTime nextRunAt;  // 다음 시도 시간

    @Lob
    @Column(columnDefinition = "TEXT")
    private String lastError;

    @PrePersist
    public void prePersist() {
        if (nextRunAt == null) {
            nextRunAt = LocalDateTime.now();
        }
    }
}
