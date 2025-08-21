package com.dataracy.modules.dataset.application.dto.response.search;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 유사 데이터 검색 결과 애플리케이션 응답 DTO
 * 검색된 데이터셋의 핵심 메타데이터를 담습니다.
 *
 * @param id 데이터 ID
 * @param title 데이터 제목
 * @param topicLabel 주제 라벨
 * @param dataSourceLabel 데이터 소스 라벨
 * @param dataTypeLabel 데이터 유형 라벨
 * @param startDate 데이터 수집 시작일
 * @param endDate 데이터 수집 종료일
 * @param description 설명
 * @param dataThumbnailUrl 썸네일 URL
 * @param downloadCount 다운로드 수
 * @param rowCount 행 개수
 * @param columnCount 열 개수
 * @param createdAt 생성 시각
 */
public record SimilarDataResponse(
        Long id,
        String title,
        String topicLabel,
        String dataSourceLabel,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String dataThumbnailUrl,
        Integer downloadCount,
        Long sizeBytes,
        Integer rowCount,
        Integer columnCount,
        LocalDateTime createdAt
) {}
