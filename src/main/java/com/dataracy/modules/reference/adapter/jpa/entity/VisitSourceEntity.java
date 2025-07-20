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

    /**
     * 주어진 값과 라벨로 새로운 VisitSourceEntity 인스턴스를 생성합니다.
     *
     * @param value 방문 소스의 고유 값
     * @param label 방문 소스의 표시 이름
     * @return 생성된 VisitSourceEntity 객체
     */
    public static VisitSourceEntity of(
            String value,
            String label
    ) {
        return VisitSourceEntity.builder()
                .value(value)
                .label(label)
                .build();
    }
}
