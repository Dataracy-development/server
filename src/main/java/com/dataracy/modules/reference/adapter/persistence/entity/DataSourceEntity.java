package com.dataracy.modules.reference.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 데이터 출처 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "data_source",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"value"})
        }
)
public class DataSourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_source_id")
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String label;

    public static DataSourceEntity toEntity(
            Long id,
            String value,
            String label
    ) {
        return DataSourceEntity.builder()
                .id(id)
                .value(value)
                .label(label)
                .build();
    }
}
