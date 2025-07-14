package com.dataracy.modules.data.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * data metadata 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "data_metadata"
)
public class DataMetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_metadata_id")
    private Long id;

    @Column(nullable = false)
    private Long row;
    @Column(nullable = false)
    private Long column;

    @Column(nullable = false)
    private String license;

    // 미리보기 (N행) JSON 문자열
    @Lob
    @Column(columnDefinition = "TEXT")
    private String previewJson;

    @Column
    private Integer qualityScore;

    public static DataMetadataEntity toEntity(
            Long row,
            Long column,
            String license,
            String previewJson,
            Integer qualityScore
    ) {
        return DataMetadataEntity.builder()
                .row(row)
                .column(column)
                .license(license)
                .previewJson(previewJson)
                .qualityScore(qualityScore)
                .build();
    }
}
