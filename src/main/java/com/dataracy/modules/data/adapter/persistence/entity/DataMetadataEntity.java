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

    /**
     * 연관된 DataEntity를 설정하고, 해당 DataEntity의 메타데이터 참조도 현재 엔티티로 동기화합니다.
     *
     * @param data 연관시킬 DataEntity 인스턴스
     */
    public void updateData(DataEntity data) {
        this.data = data;
        data.updateMetadata(this);
    }

    /**
     * 주어진 행 수, 열 수, 미리보기 JSON 문자열을 사용하여 새로운 DataMetadataEntity 인스턴스를 생성합니다.
     *
     * @param rowCount     데이터의 행 수
     * @param columnCount  데이터의 열 수
     * @param previewJson  데이터 미리보기 정보를 담은 JSON 문자열
     * @return             생성된 DataMetadataEntity 객체
     */
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
