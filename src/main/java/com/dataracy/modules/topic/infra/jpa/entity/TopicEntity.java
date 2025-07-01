package com.dataracy.modules.topic.infra.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "topic",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"domain"})
        }
)
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false)
    private String name;

    public static TopicEntity toEntity(
            Long id,
            String domain,
            String name
    ) {
        return TopicEntity.builder()
                .id(id)
                .domain(domain)
                .name(name)
                .build();
    }
}
