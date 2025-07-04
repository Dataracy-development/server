package com.dataracy.modules.user.domain.model;

import lombok.*;

/**
 * 직업 도메인 모델
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Occupation {
    private Long id;
    private String value;
    private String label;

    public static Occupation toDomain(
            Long id,
            String value,
            String label
    ) {
        return Occupation.builder()
                .id(id)
                .value(value)
                .label(label)
                .build();
    }
}
