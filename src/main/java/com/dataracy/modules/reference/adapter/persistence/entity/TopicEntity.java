package com.dataracy.modules.reference.adapter.persistence.entity;

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

    public static TopicEntity toEntity(
            String value,
            String label
    ) {
        return TopicEntity.builder()
                .value(value)
                .label(label)
                .build();
    }
}
