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
    private Long row;
    private Long column;
    private String license;
    private String previewJson;
    private Integer qualityScore;

    public static DataMetadata toDomain(
            Long id,
            Long row,
            Long column,
            String license,
            String previewJson,
            Integer qualityScore
    ) {
        return DataMetadata.builder()
                .id(id)
                .row(row)
                .column(column)
                .license(license)
                .previewJson(previewJson)
                .qualityScore(qualityScore)
                .build();
    }
}
