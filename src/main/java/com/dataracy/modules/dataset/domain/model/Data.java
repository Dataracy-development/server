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
    private String dataThumbnailUrl;

    private int downloadCount;

    private DataMetadata metadata;
    private LocalDateTime createdAt;

    /**
     * 모든 필드 값을 지정하여 새로운 Data 인스턴스를 생성합니다.
     *
     * 각 파라미터에 전달된 값으로 Data 객체의 모든 속성을 초기화합니다.
     *
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
            String dataThumbnailUrl,
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
                .dataThumbnailUrl(dataThumbnailUrl)
                .downloadCount(downloadCount)
                .metadata(metadata)
                .createdAt(createdAt)
                .build();
    }
}
