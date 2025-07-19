package com.dataracy.modules.reference.adapter.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 방문 경로 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "visit_source",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"value"})
        }
)
public class VisitSourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_source_id")
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String label;

    public static VisitSourceEntity toEntity(
            String value,
            String label
    ) {
        return VisitSourceEntity.builder()
                .value(value)
                .label(label)
                .build();
    }
}
