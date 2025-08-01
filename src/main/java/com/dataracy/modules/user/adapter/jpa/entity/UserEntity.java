package com.dataracy.modules.user.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

/**
 * users 테이블
 */
@Entity
@Where(clause = "is_deleted = false")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "providerId"}),
                @UniqueConstraint(columnNames = {"email"}),
                @UniqueConstraint(columnNames = {"nickname"})
        }
)
public class UserEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderType provider;

    @Column
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Long authorLevelId;

    @Column
    private Long occupationId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<UserTopicEntity> userTopicEntities = new ArrayList<>();

    @Column
    private Long visitSourceId;

    @Column
    private String profileImageUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAdTermsAgreed = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    /**
     * 비밀번호 변경
     *
     * @param encodedPassword 암호화된 비밀번호
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * 사용자의 관심 주제 목록에 주제 엔티티를 추가하고, 해당 주제 엔티티에 이 사용자를 할당합니다.
     *
     * @param topicEntity 추가할 사용자 주제 엔티티
     */
    public void addUserTopic(UserTopicEntity topicEntity) {
        userTopicEntities.add(topicEntity);
        topicEntity.assignUser(this);
    }

    /**
     * 주어진 정보로 새로운 UserEntity 인스턴스를 생성합니다.
     *
     * @param provider 소셜 로그인 또는 인증 제공자 유형
     * @param providerId 제공자별 사용자 식별자
     * @param role 사용자 역할 유형
     * @param email 사용자 이메일
     * @param password 암호화된 비밀번호
     * @param nickname 사용자 닉네임
     * @param authorLevelId 작성자 레벨 식별자
     * @param occupationId 직업 식별자
     * @param visitSourceId 방문 경로 식별자
     * @param profileImageUrl 프로필 이미지 URL
     * @param isAdTermsAgreed 광고 약관 동의 여부
     * @param isDeleted 삭제 여부
     * @return 생성된 UserEntity 객체
     */
    public static UserEntity of(
            ProviderType provider,
            String providerId,
            RoleType role,
            String email,
            String password,
            String nickname,
            Long authorLevelId,
            Long occupationId,
            Long visitSourceId,
            String profileImageUrl,
            Boolean isAdTermsAgreed,
            Boolean isDeleted
    ) {
        return UserEntity.builder()
                .provider(provider)
                .providerId(providerId)
                .role(role)
                .email(email)
                .password(password)
                .nickname(nickname)
                .authorLevelId(authorLevelId)
                .occupationId(occupationId)
                .visitSourceId(visitSourceId)
                .profileImageUrl(profileImageUrl)
                .isAdTermsAgreed(isAdTermsAgreed)
                .isDeleted(isDeleted)
                .build();
    }
}
