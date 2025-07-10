package com.dataracy.modules.user.adapter.persistence.entity;

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

    @Column(nullable = false)
    @Builder.Default
    private boolean isAdTermsAgreed = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    // 비밀번호 변경
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 유저의 흥미있는 도메인리스트에 도메인을 추가한다
    public void addUserTopic(UserTopicEntity topicEntity) {
        userTopicEntities.add(topicEntity);
        topicEntity.assignUser(this);
    }

    public static UserEntity toEntity(
            Long id,
            ProviderType provider,
            String providerId,
            RoleType role,
            String email,
            String password,
            String nickname,
            Long authorLevelId,
            Long occupationId,
            Long visitSourceId,
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
                .authorLevelId(authorLevelId)
                .occupationId(occupationId)
                .visitSourceId(visitSourceId)
                .isAdTermsAgreed(isAdTermsAgreed)
                .isDeleted(isDeleted)
                .build();
    }
}
