package com.dataracy.modules.dataset.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "data_es_projection_dlq")
public class DataEsProjectionDlqEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long dataId;

    @Column(nullable=false)
    @Builder.Default
    private Integer deltaDownload = 0;

    // 소프트 삭제/복원: true=삭제, false=복원
    @Column(nullable = false)
    @Builder.Default
    private Boolean setDeleted = false;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String lastError;
}
