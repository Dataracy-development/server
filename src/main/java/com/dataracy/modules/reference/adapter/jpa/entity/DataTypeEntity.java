package com.dataracy.modules.reference.adapter.jpa.entity;

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
     * 주어진 value와 label을 사용하여 새로운 DataTypeEntity 인스턴스를 생성합니다.
     *
     * @param value 데이터 타입의 고유 값
     * @param label 데이터 타입의 표시 이름
     * @return value와 label이 설정된 DataTypeEntity 인스턴스
     */
    public static DataTypeEntity of(
            String value,
            String label
    ) {
        return DataTypeEntity.builder()
                .value(value)
                .label(label)
                .build();
    }
}
