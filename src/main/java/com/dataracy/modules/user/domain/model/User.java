package com.dataracy.modules.user.domain.model;

import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    private String profileImageUrl;

    private boolean isAdTermsAgreed;
    private boolean isDeleted;

    /**
     * 주어진 원시 비밀번호가 저장된 암호화된 비밀번호와 일치하는지 확인합니다.
     *
     * @param rawPassword 사용자가 입력한 원시 비밀번호
     * @return 비밀번호가 일치하면 true, 그렇지 않으면 false
     */
    public boolean isPasswordMatch(PasswordEncoder encoder, String rawPassword) {
        return encoder.matches(rawPassword, this.password);
    }

    /**
     * 주어진 사용자 속성 값들로 새로운 User 도메인 객체를 생성합니다.
     *
     * @param id 사용자 식별자
     * @param provider 인증 제공자 유형
     * @param providerId 인증 제공자에서 발급한 식별자
     * @param role 사용자 역할
     * @param email 사용자 이메일
     * @param password 암호화된 비밀번호
     * @param nickname 사용자 닉네임
     * @param authorLevelId 작가 등급 식별자
     * @param occupationId 직업 식별자
     * @param topicIds 사용자가 연관된 토픽 ID 목록
     * @param visitSourceId 방문 경로 식별자
     * @param profileImageUrl 프로필 이미지 URL
     * @param isAdTermsAgreed 광고 약관 동의 여부
     * @param isDeleted 삭제 여부
     * @return 생성된 User 객체
     */
    public static User of(
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
            String profileImageUrl,
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
                .profileImageUrl(profileImageUrl)
                .isAdTermsAgreed(isAdTermsAgreed)
                .isDeleted(isDeleted)
                .build();
    }
}
