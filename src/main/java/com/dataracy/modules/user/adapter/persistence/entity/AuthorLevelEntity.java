package com.dataracy.modules.user.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 작성자 유형 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "author_level")
public class AuthorLevelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_level_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String label;

    public static AuthorLevelEntity toEntity(
            Long id,
            String value,
            String label
    ) {
        return AuthorLevelEntity.builder()
                .id(id)
                .value(value)
                .label(label)
                .build();
    }
}
