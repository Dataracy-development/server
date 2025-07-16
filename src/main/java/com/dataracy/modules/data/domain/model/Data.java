package com.dataracy.modules.data.domain.model;

import lombok.*;

import java.time.LocalDate;

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
    private Long authorLevelId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String analysisGuide;
    private String dataFileUrl;
    private String thumbnailUrl;
    private int downloadCount;
    private int recentWeekDownloadCount;
    private DataMetadata metadata;

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
     * 주어진 모든 필드 값을 사용하여 새로운 Data 도메인 객체를 생성합니다.
     *
     * @return 생성된 Data 객체
     */
    public static Data toDomain(
            Long id,
            String title,
            Long topicId,
            Long userId,
            Long dataSourceId,
            Long authorLevelId,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            String analysisGuide,
            String dataFileUrl,
            String thumbnailUrl,
            int downloadCount,
            int recentWeekDownloadCount,
            DataMetadata metadata
    ) {
        return Data.builder()
                .id(id)
                .title(title)
                .topicId(topicId)
                .userId(userId)
                .dataSourceId(dataSourceId)
                .authorLevelId(authorLevelId)
                .startDate(startDate)
                .endDate(endDate)
                .description(description)
                .analysisGuide(analysisGuide)
                .dataFileUrl(dataFileUrl)
                .thumbnailUrl(thumbnailUrl)
                .downloadCount(downloadCount)
                .recentWeekDownloadCount(recentWeekDownloadCount)
                .metadata(metadata)
                .build();
    }
}
