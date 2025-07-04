package com.dataracy.modules.user.adapter.persistence.entity;

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
@Table(name = "visit_source")
public class VisitSourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_source_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String label;

    public static VisitSourceEntity toEntity(
            Long id,
            String value,
            String label
    ) {
        return VisitSourceEntity.builder()
                .id(id)
                .value(value)
                .label(label)
                .build();
    }
}
