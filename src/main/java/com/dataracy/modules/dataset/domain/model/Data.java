package com.dataracy.modules.dataset.domain.model;

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
    private Long userId;

    private Long dataSourceId;
    private Long dataTypeId;
    private Long topicId;

    private LocalDate startDate;
    private LocalDate endDate;

    private String description;
    private String analysisGuide;

    private String dataFileUrl;
    private String thumbnailUrl;

    private int downloadCount;

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
     * 모든 필드 값을 지정하여 새로운 Data 객체를 생성합니다.
     *
     * 각 파라미터에 해당하는 값으로 Data 인스턴스를 초기화합니다.
     *
     * @return 지정된 값들로 생성된 Data 인스턴스
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
                .metadata(metadata)
                .createdAt(createdAt)
                .build();
    }
}
