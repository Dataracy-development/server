package com.dataracy.modules.user.domain.model;

import lombok.*;

/**
 * 작성자 유형 도메인 모델
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AuthorLevel {
    private Long id;
    private String value;
    private String label;

    public static AuthorLevel toDomain(
            Long id,
            String value,
            String label
    ) {
        return AuthorLevel.builder()
                .id(id)
                .value(value)
                .label(label)
                .build();
    }
}
