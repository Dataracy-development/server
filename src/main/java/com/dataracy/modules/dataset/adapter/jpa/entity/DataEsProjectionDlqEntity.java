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
    private Integer deltaDownload;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String error;
}
