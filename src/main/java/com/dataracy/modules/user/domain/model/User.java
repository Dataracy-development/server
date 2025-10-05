package com.dataracy.modules.user.domain.model;

import java.util.Collections;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.dataracy.modules.common.logging.support.LoggerFactory;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.vo.UserInfo;
import com.dataracy.modules.user.domain.status.UserErrorStatus;

import lombok.*;

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
  private String profileImageUrl;
  private String introductionText;
  private boolean isAdTermsAgreed;

  // 타 어그리거트는 직접 연관관계 설정을 하지 않고, ID만 보유해서 간접 참조
  private Long authorLevelId;
  private Long occupationId;
  private List<Long> topicIds;
  private Long visitSourceId;

  private boolean isDeleted;

  // SpotBugs EI_EXPOSE_REP 경고 해결을 위한 방어적 복사
  public List<Long> getTopicIds() {
    return topicIds != null ? Collections.unmodifiableList(topicIds) : Collections.emptyList();
  }

  /**
   * 입력된 원시 비밀번호가 저장된 암호화된 비밀번호와 일치하는지 검사합니다.
   *
   * @param encoder 비밀번호 일치 여부를 확인할 암호화 인코더
   * @param rawPassword 사용자가 입력한 원시 비밀번호
   * @return 비밀번호가 일치하면 true, 일치하지 않으면 false
   */
  public boolean isPasswordMatch(PasswordEncoder encoder, String rawPassword) {
    return encoder.matches(rawPassword, this.password);
  }

  /**
   * 사용자의 인증 제공자에 따라 비밀번호 변경 가능 여부를 검증합니다. GOOGLE 또는 KAKAO 제공자를 사용하는 경우 비밀번호 변경이 금지되어 있으며, 이 경우 예외를
   * 발생시킵니다.
   *
   * @throws UserException GOOGLE 또는 KAKAO 제공자일 때 비밀번호 변경이 금지된 경우 발생합니다.
   */
  public void validatePasswordChangable() {
    switch (provider) {
      case GOOGLE -> {
        LoggerFactory.domain().logRuleViolation("User Provider", "GOOGLE 유저는 비밀번호 변경이 불가합니다.");
        throw new UserException(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_GOOGLE);
      }
      case KAKAO -> {
        LoggerFactory.domain().logRuleViolation("User Provider", "KAKAO 유저는 비밀번호 변경이 불가합니다.");
        throw new UserException(UserErrorStatus.FORBIDDEN_CHANGE_PASSWORD_KAKAO);
      }
      case LOCAL -> {
        // LOCAL 유저는 비밀번호 변경 가능
      }
    }
  }

  /**
   * 현재 User 도메인 객체의 주요 정보를 UserInfo 값 객체로 변환합니다.
   *
   * @return 사용자 식별자, 역할, 이메일, 닉네임, 저자 레벨, 직업, 관심 주제, 유입 경로, 프로필 이미지 URL, 자기소개를 포함한 UserInfo 객체
   */
  public UserInfo toUserInfo() {
    return new UserInfo(
        this.id,
        this.role,
        this.email,
        this.nickname,
        this.authorLevelId,
        this.occupationId,
        this.topicIds,
        this.visitSourceId,
        this.profileImageUrl,
        this.introductionText);
  }

  /**
   * 주어진 모든 사용자 속성 값으로 새로운 User 도메인 객체를 생성합니다.
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
   * @param topicIds 사용자의 관심 토픽 ID 목록
   * @param visitSourceId 방문 경로 식별자
   * @param profileImageUrl 프로필 이미지 URL
   * @param introductionText 자기소개 텍스트
   * @param isAdTermsAgreed 광고 약관 동의 여부
   * @param isDeleted 삭제 여부
   * @return 생성된 User 객체
   *     <p>참고: 15개의 파라미터를 가지지만, User가 복잡한 도메인 모델이고 Builder 패턴을 내부적으로 사용하므로 허용됩니다.
   */
  @SuppressWarnings("java:S107") // 복잡한 도메인 모델로 많은 파라미터 필요
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
      String introductionText,
      boolean isAdTermsAgreed,
      boolean isDeleted) {
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
        .introductionText(introductionText)
        .isAdTermsAgreed(isAdTermsAgreed)
        .isDeleted(isDeleted)
        .build();
  }
}
