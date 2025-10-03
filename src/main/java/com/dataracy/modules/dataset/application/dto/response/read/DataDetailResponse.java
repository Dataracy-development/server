/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.dataset.application.dto.response.read;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 데이터셋 상세 정보 조회 애플리케이션 응답 DTO
 *
 * @param id 데이터 ID
 * @param title 데이터 제목
 * @param creatorId 작성자 아이디
 * @param creatorName 작성자 닉네임
 * @param userProfileImageUrl 데이터 업로더 유저 닉네임
 * @param userIntroductionText 데이터 업로더 유저 닉네임
 * @param authorLabel 데이터 업로드 작성자 유형 라벨
 * @param occupationLabel 데이터 업로더 직업 라벨
 * @param topicLabel 데이터 토픽 라벨
 * @param dataSourceLabel 데이터 출처 라벨
 * @param dataTypeLabel 데이터 유형 라벨
 * @param startDate 수집 시작일
 * @param endDate 수집 종료일
 * @param description 설명
 * @param analysisGuide 분석 가이드
 * @param dataThumbnailUrl 데이터 썸네일 이미지 URL
 * @param downloadCount 다운로드 수
 * @param rowCount 행 개수
 * @param columnCount 열 개수
 * @param previewJson 데이터 파일 미리보기 문자열
 * @param createdAt 생성 일시
 */
public record DataDetailResponse(
    Long id,
    String title,
    Long creatorId,
    String creatorName,
    String userProfileImageUrl,
    String userIntroductionText,
    String authorLabel,
    String occupationLabel,
    String topicLabel,
    String dataSourceLabel,
    String dataTypeLabel,
    LocalDate startDate,
    LocalDate endDate,
    String description,
    String analysisGuide,
    String dataThumbnailUrl,
    Integer downloadCount,
    Long sizeBytes,
    Integer rowCount,
    Integer columnCount,
    String previewJson,
    LocalDateTime createdAt) {}
