package com.dataracy.modules.user.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;
import com.dataracy.modules.user.application.dto.request.command.ModifyUserInfoRequest;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@SQLRestriction("is_deleted = false")
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
    private String profileImageUrl;

    @Column(nullable = false)
    private String introductionText;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAdTermsAgreed = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    /**
     * 사용자의 비밀번호를 암호화된 값으로 변경합니다.
     *
     * @param encodedPassword 새로 설정할 암호화된 비밀번호
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
     * 사용자 엔티티의 프로필 관련 필드들을 요청 DTO의 값으로 갱신한다.
     *
     * 수정되는 필드: nickname, authorLevelId, occupationId, visitSourceId, introductionText.
     *
     * @param requestDto 갱신할 닉네임, 작가 레벨 ID, 직업 ID, 방문 경로 ID 및 소개 텍스트를 제공하는 DTO
     */
    public void modifyUserInfo(ModifyUserInfoRequest requestDto) {
        this.nickname = requestDto.nickname();
        this.authorLevelId = requestDto.authorLevelId();
        this.occupationId = requestDto.occupationId();
        this.visitSourceId = requestDto.visitSourceId();
        this.introductionText = requestDto.introductionText();
    }

    /**
     * 사용자 엔터티의 프로필 이미지 URL을 변경합니다.
     *
     * @param profileImageUrl 새 프로필 이미지의 URL 문자열
     */
    public void modifyProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * 주어진 사용자 정보를 기반으로 새로운 UserEntity 인스턴스를 생성합니다.
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
     * @param introductionText 사용자 소개 문구
     * @param isAdTermsAgreed 광고 약관 동의 여부
     * @param isDeleted 삭제 여부
     * @return 생성된 UserEntity 객체
     * 
     * 참고: 13개의 파라미터를 가지지만, UserEntity가 복잡한 도메인 엔티티이고 Builder 패턴을 내부적으로 사용하므로 허용됩니다.
     */
    @SuppressWarnings("java:S107") // 복잡한 도메인 엔티티로 많은 파라미터 필요
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
            String introductionText,
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
                .introductionText(introductionText)
                .isAdTermsAgreed(isAdTermsAgreed)
                .isDeleted(isDeleted)
                .build();
    }
}
