package com.dataracy.modules.user.domain.model;

import lombok.*;

/**
 * 방문 경로 엔티티
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class VisitSource {
    private Long id;
    private String value;
    private String label;

    public static VisitSource toDomain(
            Long id,
            String value,
            String label
    ) {
        return VisitSource.builder()
                .id(id)
                .value(value)
                .label(label)
                .build();
    }
}
