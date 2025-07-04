package com.dataracy.modules.user.adapter.persistence.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import com.dataracy.modules.user.adapter.persistence.entity.reference.AuthorLevelEntity;
import com.dataracy.modules.user.adapter.persistence.entity.reference.OccupationEntity;
import com.dataracy.modules.user.adapter.persistence.entity.reference.VisitSourceEntity;
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
    @Column(name = "user_id", nullable = false)
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_level_id", nullable = false)
    private AuthorLevelEntity authorLevel;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_level_id")
    private OccupationEntity occupation;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<UserTopicEntity> userTopicEntities = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_source_id")
    private VisitSourceEntity visitSource;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAdTermsAgreed = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    public void addUserTopic(UserTopicEntity topicEntity) {
        userTopicEntities.add(topicEntity);
        topicEntity.setUser(this);
    }

    public static UserEntity toEntity(
            Long id,
            ProviderType provider,
            String providerId,
            RoleType role,
            String email,
            String password,
            String nickname,
            AuthorLevelEntity authorLevelEntity,
            OccupationEntity occupationEntity,
            VisitSourceEntity visitSourceEntity,
            Boolean isAdTermsAgreed,
            Boolean isDeleted
    ) {
        return UserEntity.builder()
                .id(id)
                .provider(provider)
                .providerId(providerId)
                .role(role)
                .email(email)
                .password(password)
                .nickname(nickname)
                .authorLevel(authorLevelEntity)
                .occupation(occupationEntity)
                .visitSource(visitSourceEntity)
                .isAdTermsAgreed(isAdTermsAgreed)
                .isDeleted(isDeleted)
                .build();
    }
}
