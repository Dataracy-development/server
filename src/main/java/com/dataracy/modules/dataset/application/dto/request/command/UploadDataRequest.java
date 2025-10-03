/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.dto.request.command;

import java.time.LocalDate;

/**
 * 데이터 업로드 애플리케이션 요청 DTO.
 *
 * @param title 데이터 제목
 * @param topicId 주제 ID
 * @param dataTypeId 데이터 타입 ID
 * @param dataSourceId 데이터 소스 ID
 * @param startDate 데이터 기간 시작일(포함)
 * @param endDate 데이터 기간 종료일(포함)
 * @param description 데이터 설명
 * @param analysisGuide 분석 가이드/권장 분석 방법
 */
public record UploadDataRequest(
    String title,
    Long topicId,
    Long dataSourceId,
    Long dataTypeId,
    LocalDate startDate,
    LocalDate endDate,
    String description,
    String analysisGuide) {}
