package com.dataracy.modules.dataset.application.dto.response.read;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 연결된 데이터 조회 애플리케이션 응답 DTO
 * 데이터셋과 연결된 프로젝트 수, 다운로드 수 등 요약 정보를 담습니다.
 *
 * @param id 데이터 ID
 * @param title 데이터 제목
 * @param creatorId 작성자 아이디
 * @param creatorName 작성자 닉네임
 * @param userProfileImageUrl 데이터 업로더 유저 닉네임
 * @param topicLabel 주제 라벨
 * @param dataTypeLabel 데이터 유형 라벨
 * @param startDate 수집 시작일
 * @param endDate 수집 종료일
 * @param dataThumbnailUrl 썸네일 URL
 * @param downloadCount 다운로드 수
 * @param rowCount 행 개수
 * @param columnCount 열 개수
 * @param createdAt 생성 일시
 * @param countConnectedProjects 연결된 프로젝트 수
 */
public record ConnectedDataResponse(
        Long id,
        String title,
        Long creatorId,
        String creatorName,
        String userProfileImageUrl,
        String topicLabel,
        String dataTypeLabel,
        LocalDate startDate,
        LocalDate endDate,
        String dataThumbnailUrl,
        Integer downloadCount,
        Long sizeBytes,
        Integer rowCount,
        Integer columnCount,
        LocalDateTime createdAt,
        Long countConnectedProjects
) {}
