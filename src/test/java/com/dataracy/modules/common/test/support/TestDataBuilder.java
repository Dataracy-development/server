package com.dataracy.modules.common.test.support;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import com.dataracy.modules.comment.domain.model.Comment;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.model.Like;
import com.dataracy.modules.project.adapter.jpa.entity.ProjectEntity;
import com.dataracy.modules.project.domain.model.Project;
import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;

/**
 * 테스트 데이터 생성을 위한 빌더 패턴 유틸리티 클래스
 *
 * <p>사용법: User user = TestDataBuilder.user() .email("test@example.com") .nickname("테스트유저")
 * .role(RoleType.ROLE_USER) .build();
 */
public class TestDataBuilder {

  private TestDataBuilder() {
    // Utility class
  }

  /** User 테스트 데이터 빌더 */
  public static UserBuilder user() {
    return new UserBuilder();
  }

  /** UserEntity 테스트 데이터 빌더 */
  public static UserEntityBuilder userEntity() {
    return new UserEntityBuilder();
  }

  /** Like 테스트 데이터 빌더 */
  public static LikeBuilder like() {
    return new LikeBuilder();
  }

  /** Project 테스트 데이터 빌더 */
  public static ProjectBuilder project() {
    return new ProjectBuilder();
  }

  /** ProjectEntity 테스트 데이터 빌더 */
  public static ProjectEntityBuilder projectEntity() {
    return new ProjectEntityBuilder();
  }

  /** Comment 테스트 데이터 빌더 */
  public static CommentBuilder comment() {
    return new CommentBuilder();
  }

  /** UserEntity 빌더 클래스 */
  public static class UserEntityBuilder {
    private Long id = 1L;
    private String email = "test@example.com";
    private String nickname = "테스트유저";
    private String password = "password1!";
    private RoleType role = RoleType.ROLE_USER;
    private ProviderType provider = ProviderType.LOCAL;
    private String providerId = "provider1";
    private Long authorLevelId = 1L;
    private Long occupationId = 1L;
    private Long visitSourceId = 1L;
    private String profileImageUrl = "https://example.com/profile.jpg";
    private String introductionText = "안녕하세요!";
    private boolean isAdTermsAgreed = true;
    private boolean isDeleted = false;

    public UserEntityBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public UserEntityBuilder email(String email) {
      this.email = email;
      return this;
    }

    public UserEntityBuilder nickname(String nickname) {
      this.nickname = nickname;
      return this;
    }

    public UserEntityBuilder password(String password) {
      this.password = password;
      return this;
    }

    public UserEntityBuilder role(RoleType role) {
      this.role = role;
      return this;
    }

    public UserEntityBuilder provider(ProviderType provider) {
      this.provider = provider;
      return this;
    }

    public UserEntityBuilder providerId(String providerId) {
      this.providerId = providerId;
      return this;
    }

    public UserEntityBuilder authorLevelId(Long authorLevelId) {
      this.authorLevelId = authorLevelId;
      return this;
    }

    public UserEntityBuilder occupationId(Long occupationId) {
      this.occupationId = occupationId;
      return this;
    }

    public UserEntityBuilder visitSourceId(Long visitSourceId) {
      this.visitSourceId = visitSourceId;
      return this;
    }

    public UserEntityBuilder profileImageUrl(String profileImageUrl) {
      this.profileImageUrl = profileImageUrl;
      return this;
    }

    public UserEntityBuilder introductionText(String introductionText) {
      this.introductionText = introductionText;
      return this;
    }

    public UserEntityBuilder isAdTermsAgreed(boolean isAdTermsAgreed) {
      this.isAdTermsAgreed = isAdTermsAgreed;
      return this;
    }

    public UserEntityBuilder isDeleted(boolean isDeleted) {
      this.isDeleted = isDeleted;
      return this;
    }

    public UserEntity build() {
      return UserEntity.builder()
          .id(id)
          .email(email)
          .nickname(nickname)
          .password(password)
          .role(role)
          .provider(provider)
          .providerId(providerId)
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

  /** User 빌더 클래스 */
  public static class UserBuilder {
    private Long id = 1L;
    private String email = "test@example.com";
    private String nickname = "테스트유저";
    private String password = "password1!";
    private RoleType role = RoleType.ROLE_USER;
    private ProviderType provider = ProviderType.LOCAL;
    private String providerId = "provider1";

    public UserBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public UserBuilder email(String email) {
      this.email = email;
      return this;
    }

    public UserBuilder nickname(String nickname) {
      this.nickname = nickname;
      return this;
    }

    public UserBuilder password(String password) {
      this.password = password;
      return this;
    }

    public UserBuilder role(RoleType role) {
      this.role = role;
      return this;
    }

    public UserBuilder provider(ProviderType provider) {
      this.provider = provider;
      return this;
    }

    public UserBuilder providerId(String providerId) {
      this.providerId = providerId;
      return this;
    }

    public User build() {
      return User.builder()
          .id(id)
          .email(email)
          .nickname(nickname)
          .password(password)
          .role(role)
          .provider(provider)
          .providerId(providerId)
          .build();
    }
  }

  /** Like 빌더 클래스 */
  public static class LikeBuilder {
    private Long id = 1L;
    private Long targetId = 100L;
    private TargetType targetType = TargetType.PROJECT;
    private Long userId = 1L;

    public LikeBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public LikeBuilder targetId(Long targetId) {
      this.targetId = targetId;
      return this;
    }

    public LikeBuilder targetType(TargetType targetType) {
      this.targetType = targetType;
      return this;
    }

    public LikeBuilder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    public Like build() {
      return Like.of(id, targetId, targetType, userId);
    }
  }

  /** ProjectEntity 빌더 클래스 */
  public static class ProjectEntityBuilder {
    private Long id = 1L;
    private String title = "테스트 프로젝트";
    private Long userId = 1L;
    private Long analysisPurposeId = 1L;
    private Long dataSourceId = 1L;
    private Long authorLevelId = 1L;
    private Long topicId = 1L;
    private Boolean isContinue = true;
    private String content = "테스트 프로젝트 내용";
    private String thumbnailUrl = "https://example.com/thumbnail.jpg";
    private Long commentCount = 0L;
    private Long likeCount = 0L;
    private Long viewCount = 0L;
    private boolean isDeleted = false;

    public ProjectEntityBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public ProjectEntityBuilder title(String title) {
      this.title = title;
      return this;
    }

    public ProjectEntityBuilder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    public ProjectEntityBuilder analysisPurposeId(Long analysisPurposeId) {
      this.analysisPurposeId = analysisPurposeId;
      return this;
    }

    public ProjectEntityBuilder dataSourceId(Long dataSourceId) {
      this.dataSourceId = dataSourceId;
      return this;
    }

    public ProjectEntityBuilder authorLevelId(Long authorLevelId) {
      this.authorLevelId = authorLevelId;
      return this;
    }

    public ProjectEntityBuilder topicId(Long topicId) {
      this.topicId = topicId;
      return this;
    }

    public ProjectEntityBuilder isContinue(Boolean isContinue) {
      this.isContinue = isContinue;
      return this;
    }

    public ProjectEntityBuilder content(String content) {
      this.content = content;
      return this;
    }

    public ProjectEntityBuilder thumbnailUrl(String thumbnailUrl) {
      this.thumbnailUrl = thumbnailUrl;
      return this;
    }

    public ProjectEntityBuilder commentCount(Long commentCount) {
      this.commentCount = commentCount;
      return this;
    }

    public ProjectEntityBuilder likeCount(Long likeCount) {
      this.likeCount = likeCount;
      return this;
    }

    public ProjectEntityBuilder viewCount(Long viewCount) {
      this.viewCount = viewCount;
      return this;
    }

    public ProjectEntityBuilder isDeleted(boolean isDeleted) {
      this.isDeleted = isDeleted;
      return this;
    }

    public ProjectEntity build() {
      return ProjectEntity.builder()
          .id(id)
          .title(title)
          .userId(userId)
          .analysisPurposeId(analysisPurposeId)
          .dataSourceId(dataSourceId)
          .authorLevelId(authorLevelId)
          .topicId(topicId)
          .isContinue(isContinue)
          .content(content)
          .thumbnailUrl(thumbnailUrl)
          .commentCount(commentCount)
          .likeCount(likeCount)
          .viewCount(viewCount)
          .isDeleted(isDeleted)
          .build();
    }
  }

  /** Project 빌더 클래스 */
  public static class ProjectBuilder {
    private Long id = 1L;
    private String title = "테스트 프로젝트";
    private Long userId = 1L;

    public ProjectBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public ProjectBuilder title(String title) {
      this.title = title;
      return this;
    }

    public ProjectBuilder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    public Project build() {
      return Project.builder().id(id).title(title).userId(userId).build();
    }
  }

  /** Comment 빌더 클래스 */
  public static class CommentBuilder {
    private Long id = 1L;
    private String content = "테스트 댓글";
    private Long userId = 1L;
    private Long projectId = 100L;
    private Long parentCommentId = null;
    private Long likeCount = 0L;

    public CommentBuilder id(Long id) {
      this.id = id;
      return this;
    }

    public CommentBuilder content(String content) {
      this.content = content;
      return this;
    }

    public CommentBuilder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    public CommentBuilder projectId(Long projectId) {
      this.projectId = projectId;
      return this;
    }

    public CommentBuilder parentCommentId(Long parentCommentId) {
      this.parentCommentId = parentCommentId;
      return this;
    }

    public CommentBuilder likeCount(Long likeCount) {
      this.likeCount = likeCount;
      return this;
    }

    public Comment build() {
      return Comment.builder()
          .id(id)
          .content(content)
          .userId(userId)
          .projectId(projectId)
          .parentCommentId(parentCommentId)
          .likeCount(likeCount)
          .createdAt(LocalDateTime.now())
          .build();
    }
  }

  /** 랜덤 데이터 생성 유틸리티 */
  public static class RandomData {
    public static String randomEmail() {
      return "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    public static String randomNickname() {
      return "유저" + UUID.randomUUID().toString().substring(0, 6);
    }

    public static Long randomId() {
      return Math.abs(new Random().nextLong() % 10000) + 1;
    }

    public static String randomString(int length) {
      return UUID.randomUUID().toString().substring(0, Math.min(length, 36));
    }
  }
}
