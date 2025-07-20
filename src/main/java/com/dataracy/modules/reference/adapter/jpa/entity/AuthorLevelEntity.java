package com.dataracy.modules.reference.adapter.jpa.entity;

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
@Table(
        name = "author_level",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"value"})
        }
)
public class AuthorLevelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_level_id")
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String label;

    /**
     * 주어진 값과 라벨로 새로운 AuthorLevelEntity 인스턴스를 생성합니다.
     *
     * @param value 엔터티의 value 필드에 할당될 값
     * @param label 엔터티의 label 필드에 할당될 라벨
     * @return 지정된 value와 label을 가진 AuthorLevelEntity 인스턴스
     */
    public static AuthorLevelEntity of(
            String value,
            String label
    ) {
        return AuthorLevelEntity.builder()
                .value(value)
                .label(label)
                .build();
    }
}
