package com.dataracy.modules.reference.adapter.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 토픽 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "topic",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"value"})
        }
)
public class TopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String label;

    /**
     * 주어진 값과 라벨로 새로운 TopicEntity 인스턴스를 생성합니다.
     *
     * @param value 토픽의 고유 값
     * @param label 토픽의 표시 이름
     * @return 생성된 TopicEntity 객체
     */
    public static TopicEntity of(
            String value,
            String label
    ) {
        return TopicEntity.builder()
                .value(value)
                .label(label)
                .build();
    }
}
