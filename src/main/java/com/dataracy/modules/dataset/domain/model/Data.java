package com.dataracy.modules.dataset.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private Long sizeBytes;
    private DataMetadata metadata;
    private LocalDateTime createdAt;

    /**
     * 모든 필드 값을 사용해 Data 객체를 생성하여 반환합니다.
     *
     * 주어진 인자들로 Data의 모든 속성을 초기화한 새 인스턴스를 빌더로 구성해 반환합니다.
     * 
     * 참고: 15개의 파라미터를 가지지만, Data가 복잡한 도메인 모델이고 Builder 패턴을 내부적으로 사용하므로 허용됩니다.
     *
     * @param sizeBytes 데이터 파일의 크기(바이트 단위). 값이 없을 수 있으므로 null 허용.
     * @return 지정된 값들로 초기화된 Data 객체
     */
    @SuppressWarnings("java:S107") // 복잡한 도메인 모델로 많은 파라미터 필요
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
            Long sizeBytes,
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
                .sizeBytes(sizeBytes)
                .metadata(metadata)
                .createdAt(createdAt)
                .build();
    }
}
