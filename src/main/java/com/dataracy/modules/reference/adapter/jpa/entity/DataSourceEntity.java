package com.dataracy.modules.reference.adapter.jpa.entity;

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

    /**
     * 주어진 id, value, label 값을 사용하여 DataSourceEntity 인스턴스를 생성합니다.
     *
     * @param value 데이터 소스의 값
     * @param label 데이터 소스의 라벨
     * @return 생성된 DataSourceEntity 객체
     */
    public static DataSourceEntity toEntity(
            String value,
            String label
    ) {
        return DataSourceEntity.builder()
                .value(value)
                .label(label)
                .build();
    }
}
