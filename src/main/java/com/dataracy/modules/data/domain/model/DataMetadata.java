package com.dataracy.modules.data.domain.model;

import lombok.*;

/**
 * 데이터의 메타데이터 도메인 모델
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DataMetadata {
    private Long id;
    private Integer rowCount;
    private Integer columnCount;
    private String previewJson;

    public static DataMetadata toDomain(
            Long id,
            Integer rowCount,
            Integer columnCount,
            String previewJson
    ) {
        return DataMetadata.builder()
                .id(id)
                .rowCount(rowCount)
                .columnCount(columnCount)
                .previewJson(previewJson)
                .build();
    }
}
