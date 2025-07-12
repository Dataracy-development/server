package com.dataracy.modules.reference.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 분석 목적 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "analysis_purpose",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"value"})
        }
)
public class AnalysisPurposeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_purpose_id")
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String label;

    /**
     * 주어진 id, value, label 값을 사용하여 AnalysisPurposeEntity 인스턴스를 생성합니다.
     *
     * @param value 분석 목적의 고유 값
     * @param label 분석 목적의 표시 이름
     * @return 생성된 AnalysisPurposeEntity 객체
     */
    public static AnalysisPurposeEntity toEntity(
            String value,
            String label
    ) {
        return AnalysisPurposeEntity.builder()
                .value(value)
                .label(label)
                .build();
    }
}
