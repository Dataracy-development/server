/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.adapter.jpa.entity;

import java.time.LocalDateTime;

import com.dataracy.modules.common.base.BaseTimeEntity;
import com.dataracy.modules.dataset.domain.enums.DataEsProjectionType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "data_es_projection_queue",
    indexes = {
      @Index(name = "idx_data_proj_status_next_run_at", columnList = "status,nextRunAt"),
      @Index(name = "idx_data_proj_data_id", columnList = "dataId")
    })
public class DataEsProjectionTaskEntity extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long dataId;

  // 다운로드 수 증감: 증가(+1), 감소(-1), 미사용(0)
  @Column(nullable = false)
  @Builder.Default
  private Integer deltaDownload = 0;

  // 소프트 삭제/복원: null=해당 없음, true=삭제, false=복원
  @Column(nullable = false)
  @Builder.Default
  private Boolean setDeleted = false;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private DataEsProjectionType status = DataEsProjectionType.PENDING;

  @Column(nullable = false)
  @Builder.Default
  private Integer retryCount = 0;

  @Column(nullable = false)
  private LocalDateTime nextRunAt;

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
