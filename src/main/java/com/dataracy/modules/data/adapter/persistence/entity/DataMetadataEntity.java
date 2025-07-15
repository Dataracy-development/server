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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_id", nullable = false)
    private DataEntity data;

    @Column(nullable = false)
    private Integer rowCount;
    @Column(nullable = false)
    private Integer columnCount;

    // 미리보기 (N행) JSON 문자열
    @Lob
    @Column(columnDefinition = "TEXT")
    private String previewJson;

    @Column
    private Integer qualityScore;

    public static DataMetadataEntity toEntity(
            Integer rowCount,
            Integer columnCount,
            String previewJson
    ) {
        return DataMetadataEntity.builder()
                .rowCount(rowCount)
                .columnCount(columnCount)
                .previewJson(previewJson)
                .build();
    }
}
