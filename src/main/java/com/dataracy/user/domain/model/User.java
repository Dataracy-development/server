package com.dataracy.user.domain.model;

import com.dataracy.user.domain.enums.*;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

    private Long id;

    private ProviderStatusType provider;
    private String providerId;
    private RoleStatusType role;

    private String email;
    private String password;

    private String nickname;
    private AuthorLevelStatusType authorLevel;
    private OccupationStatusType occupation;
    private List<InterestDomainStatusType> domains;
    private VisitSourceStatusType visitSource;
    private Boolean isAdTermsAgreed;

    private Boolean isDeleted;

    public static User toDomain(
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
        return User.builder()
                .id(id)
                .provider(provider)
                .providerId(providerId)
                .role(role)
                .email(email)
                .password(password)
                .nickname(nickname)
                .authorLevel(authorLevel)
                .occupation(occupation)
                .domains(domains)
                .visitSource(visitSource)
                .isAdTermsAgreed(isAdTermsAgreed)
                .isDeleted(isDeleted)
                .build();
    }
}
