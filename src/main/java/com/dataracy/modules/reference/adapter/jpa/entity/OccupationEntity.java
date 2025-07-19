package com.dataracy.modules.reference.adapter.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 직업 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "occupation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"value"})
        }
)
public class OccupationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "occupation_id")
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String label;

    public static OccupationEntity toEntity(
            String value,
            String label
    ) {
        return OccupationEntity.builder()
                .value(value)
                .label(label)
                .build();
    }
}
