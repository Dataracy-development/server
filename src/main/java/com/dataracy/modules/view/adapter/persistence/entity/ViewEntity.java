package com.dataracy.modules.view.adapter.persistence.entity;

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
     * 주어진 값들로 새로운 ViewEntity 인스턴스를 생성합니다.
     *
     * @param projectId 프로젝트 식별자
     * @param userId 사용자 식별자 (회원이 아닐 경우 null)
     * @param anonymousId 비회원 식별용 UUID (회원일 경우 null)
     * @param ip 접속한 사용자의 IP 주소
     * @param userAgent 접속한 사용자의 User-Agent 문자열
     * @return 생성된 ViewEntity 객체
     */
    public static ViewEntity toEntity(
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
