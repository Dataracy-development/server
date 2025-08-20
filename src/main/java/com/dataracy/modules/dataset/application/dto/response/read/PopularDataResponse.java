package com.dataracy.modules.dataset.application.dto.response.read;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 인기있는 데이터셋 조회 애플리케이션 응답 DTO
 *
 * @param id 데이터 ID
 * @param title 데이터 제목
 * @param username 데이터 업로더 유저 닉네임
 * @param topicLabel 데이터 토픽 라벨
 * @param dataSourceLabel 데이터 출처 라벨
 * @param dataTypeLabel 데이터 유형 라벨
 * @param startDate 수집 시작일
 * @param endDate 수집 종료일
 * @param description 설명
 * @param dataThumbnailUrl 데이터 썸네일 이미지 URL
 * @param downloadCount 다운로드 수
 * @param rowCount 행 개수
 * @param columnCount 열 개수
 * @param createdAt 생성 일시
 * @param countConnectedProjects 연결된 프로젝트 수
 */
public record PopularDataResponse(
        Long id,
        String title,
        String username,
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
        LocalDateTime createdAt,
        Long countConnectedProjects
) {}
