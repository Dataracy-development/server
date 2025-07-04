package com.dataracy.modules.topic.domain.model;

import lombok.*;

/**
 * 토픽 도메인 모델
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Topic {
    private Long id;
    private String value;
    private String label;

    public static Topic toDomain(
            Long id,
            String value,
            String label
    ) {
        return Topic.builder()
                .id(id)
                .value(value)
                .label(label)
                .build();
    }
}
