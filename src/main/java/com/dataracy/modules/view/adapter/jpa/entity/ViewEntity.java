package com.dataracy.modules.view.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "project_view")
public class ViewEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_id")
    private Long id;

    // 타 어그리거트로 간접참조
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "anonymous_id")
    private String anonymousId; // 비회원 식별자 (UUID)

    @Column(name = "ip")
    private String ip;

    @Column(name = "user_agent")
    private String userAgent;

    /**
     * 프로젝트 조회 기록을 나타내는 ViewEntity 인스턴스를 생성합니다.
     *
     * @param projectId 조회가 발생한 프로젝트의 식별자
     * @param userId 회원 사용자의 식별자 (비회원일 경우 null)
     * @param anonymousId 비회원 사용자를 구분하는 UUID (회원일 경우 null)
     * @param ip 사용자의 IP 주소
     * @param userAgent 사용자의 User-Agent 문자열
     * @return 생성된 ViewEntity 객체
     */
    public static ViewEntity of(
            Long projectId,
            Long userId,
            String anonymousId,
            String ip,
            String userAgent
    ) {
        return ViewEntity.builder()
                .projectId(projectId)
                .userId(userId)
                .anonymousId(anonymousId)
                .ip(ip)
                .userAgent(userAgent)
                .build();
    }
}
