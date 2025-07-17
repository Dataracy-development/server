package com.dataracy.modules.reference.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 데이터 유형 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "data_type",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"value"})
        }
)
public class DataTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_type_id")
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String label;

    /**
     * 주어진 id, value, label 값을 사용하여 DataTypeEntity 인스턴스를 생성합니다.
     *
     * @param value 데이터 유형의 고유 값
     * @param label 데이터 유형의 표시 이름
     * @return 생성된 DataTypeEntity 객체
     */
    public static DataTypeEntity toEntity(
            String value,
            String label
    ) {
        return DataTypeEntity.builder()
                .value(value)
                .label(label)
                .build();
    }
}
