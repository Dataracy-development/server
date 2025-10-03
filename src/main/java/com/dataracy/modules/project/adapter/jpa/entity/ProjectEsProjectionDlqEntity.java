/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
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

  @Column(nullable = false)
  private Long projectId;

  @Column(nullable = false)
  @Builder.Default
  private Integer deltaComment = 0;

  @Column(nullable = false)
  @Builder.Default
  private Integer deltaLike = 0;

  @Column(nullable = false)
  @Builder.Default
  private Long deltaView = 0L;

  @Column(nullable = false)
  @Builder.Default
  private Boolean setDeleted = false;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String lastError;
}
