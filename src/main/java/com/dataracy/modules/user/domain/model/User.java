package com.dataracy.modules.user.domain.model;

import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.*;

import java.util.List;

/**
 * 유저 도메인 모델
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

    private Long id;

    private ProviderType provider;
    private String providerId;
    private RoleType role;

    private String email;
    private String password;

    private String nickname;
    private Long authorLevelId;
    private Long occupationId;

    // 타 어그리거트인 Topic 자체를 직접 들고 있지 않고, ID만 보유해서 간접 참조
    private List<Long> topicIds;

    private Long visitSourceId;

    private boolean isAdTermsAgreed;
    private boolean isDeleted;

    public static User toDomain(
            Long id,
            ProviderType provider,
            String providerId,
            RoleType role,
            String email,
            String password,
            String nickname,
            Long authorLevelId,
            Long occupationId,
            List<Long> topicIds,
            Long visitSourceId,
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
                .authorLevelId(authorLevelId)
                .occupationId(occupationId)
                .topicIds(topicIds)
                .visitSourceId(visitSourceId)
                .isAdTermsAgreed(isAdTermsAgreed)
                .isDeleted(isDeleted)
                .build();
    }
}
