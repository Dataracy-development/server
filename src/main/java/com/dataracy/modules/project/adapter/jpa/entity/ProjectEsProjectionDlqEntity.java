package com.dataracy.modules.project.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "project_es_projection_dlq")
public class ProjectEsProjectionDlqEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long projectId;

    @Column(nullable=false)
    private Integer deltaComment;

    @Column(nullable=false)
    private Integer deltaLike;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String error;
}
