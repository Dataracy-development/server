package com.dataracy.modules.project.adapter.persistence.entity.reference;

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

    public static AnalysisPurposeEntity toEntity(
            Long id,
            String value,
            String label
    ) {
        return AnalysisPurposeEntity.builder()
                .id(id)
                .value(value)
                .label(label)
                .build();
    }
}
