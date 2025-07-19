package com.dataracy.modules.data.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 데이터 도메인 모델
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Data {
    private Long id;

    private String title;
    private Long topicId;
    private Long userId;
    private Long dataSourceId;
    private Long dataTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String analysisGuide;
    private String dataFileUrl;
    private String thumbnailUrl;
    private int downloadCount;
    private int recentWeekDownloadCount;
    private DataMetadata metadata;
    private LocalDateTime createdAt;

    /**
     * 데이터 파일 URL을 갱신합니다.
     *
     * @param dataFileUrl 새로 설정할 데이터 파일의 URL
     */
    public void updateDataFileUrl (String dataFileUrl) {
        this.dataFileUrl = dataFileUrl;
    }

    /**
     * 데이터의 썸네일 파일 URL을 업데이트합니다.
     *
     * @param thumbnailFileUrl 새로 설정할 썸네일 파일의 URL
     */
    public void updateThumbnailFileUrl (String thumbnailFileUrl) {
        this.thumbnailUrl = thumbnailFileUrl;
    }

    /**
     * 모든 필드 값을 지정하여 새로운 Data 도메인 객체를 생성합니다.
     *
     * @param id 데이터의 고유 식별자
     * @param title 데이터 제목
     * @param topicId 주제 식별자
     * @param userId 사용자 식별자
     * @param dataSourceId 데이터 소스 식별자
     * @param dataTypeId 데이터 타입 식별자
     * @param startDate 데이터의 시작 날짜
     * @param endDate 데이터의 종료 날짜
     * @param description 데이터 설명
     * @param analysisGuide 데이터 분석 가이드
     * @param dataFileUrl 데이터 파일의 URL
     * @param thumbnailUrl 썸네일 이미지의 URL
     * @param downloadCount 전체 다운로드 횟수
     * @param recentWeekDownloadCount 최근 1주일간 다운로드 횟수
     * @param metadata 데이터 메타정보
     * @param createdAt 데이터 생성 시각
     * @return 지정된 값들로 초기화된 Data 객체
     */
    public static Data of(
            Long id,
            String title,
            Long topicId,
            Long userId,
            Long dataSourceId,
            Long dataTypeId,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            String analysisGuide,
            String dataFileUrl,
            String thumbnailUrl,
            int downloadCount,
            int recentWeekDownloadCount,
            DataMetadata metadata,
            LocalDateTime createdAt
    ) {
        return Data.builder()
                .id(id)
                .title(title)
                .topicId(topicId)
                .userId(userId)
                .dataSourceId(dataSourceId)
                .dataTypeId(dataTypeId)
                .startDate(startDate)
                .endDate(endDate)
                .description(description)
                .analysisGuide(analysisGuide)
                .dataFileUrl(dataFileUrl)
                .thumbnailUrl(thumbnailUrl)
                .downloadCount(downloadCount)
                .recentWeekDownloadCount(recentWeekDownloadCount)
                .metadata(metadata)
                .createdAt(createdAt)
                .build();
    }
}
