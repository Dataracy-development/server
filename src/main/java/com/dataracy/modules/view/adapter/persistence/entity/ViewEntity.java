package com.dataracy.modules.view.adapter.persistence.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "view")
public class ViewEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_id")
    private Long id;

    // 타 어그리거트로 간접참조
    @Column(name = "projectId", nullable = false)
    private Long projectId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "anonymous_id")
    private String anonymousId; // 비회원 식별자 (UUID)

    @Column(name = "ip")
    private String ip;

    @Column(name = "user_agent")
    private String userAgent;
}
