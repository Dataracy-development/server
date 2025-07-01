package com.dataracy.modules.topic.domain.model;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Topic {
    private Long id;
    private String domain;
    private String name;

    public static Topic toDomain(
            Long id,
            String domain,
            String name
    ) {
        return Topic.builder()
                .id(id)
                .domain(domain)
                .name(name)
                .build();
    }
}
