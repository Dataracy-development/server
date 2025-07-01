package com.dataracy.modules.user.domain.model;

import com.dataracy.modules.user.domain.enums.*;
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

    // 다른 어그리거트 Topic 자체를 직접 들고 있지 않고, ID만 보유해서 간접 참조
    private List<Long> topicIds;

    private VisitSourceStatusType visitSource;
    private boolean isAdTermsAgreed;

    private boolean isDeleted;

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
            List<Long> topicIds,
            VisitSourceStatusType visitSource,
            boolean isAdTermsAgreed,
            boolean isDeleted
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
                .topicIds(topicIds)
                .visitSource(visitSource)
                .isAdTermsAgreed(isAdTermsAgreed)
                .isDeleted(isDeleted)
                .build();
    }
}
