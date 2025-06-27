package com.dataracy.user.infra.jpa.entity;

import com.dataracy.common.domain.BaseTimeEntity;
import com.dataracy.user.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.List;

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
    private ProviderStatusType provider;

    @Column
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleStatusType role;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthorLevelStatusType authorLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OccupationStatusType occupation;

//    @Column
//    private List<InterestDomainStatusType> domains;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitSourceStatusType visitSource;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAdTermsAgreed = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    public static UserEntity toEntity(
            Long id,
            ProviderStatusType provider,
            String providerId,
            RoleStatusType role,
            String email,
            String password,
            String nickname,
            AuthorLevelStatusType authorLevel,
            OccupationStatusType occupation,
            List<InterestDomainStatusType> domains,
            VisitSourceStatusType visitSource,
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
                .authorLevel(authorLevel)
                .occupation(occupation)
//                .domains(domains)
                .visitSource(visitSource)
                .isAdTermsAgreed(isAdTermsAgreed)
                .isDeleted(isDeleted)
                .build();
    }
}
