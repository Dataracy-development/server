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

    // 다운로드 수 증감: 증가(+1), 감소(-1), 미사용(null)
    @Column
    private Integer deltaDownload;

    // 소프트 삭제/복원: null=해당 없음, true=삭제, false=복원
    @Column(name = "set_deleted")
    private Boolean setDeleted;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String lastError;
}
