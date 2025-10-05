package com.dataracy.modules.user.adapter.jpa.entity;

import com.dataracy.modules.common.base.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "user_topic")
public class UserTopicEntity extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_topic_id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(nullable = false)
  private Long topicId;

  /**
   * 유저 - 토픽 연결 테이블을 생성하여 연관 관계를 설정한다.
   *
   * @param user 유저
   * @param topicId 주제 아이디
   * @return 생성된 UserTopicEntity 객체
   */
  public static UserTopicEntity of(UserEntity user, Long topicId) {
    UserTopicEntity userTopicEntity = UserTopicEntity.builder().topicId(topicId).build();
    userTopicEntity.assignUser(user);
    return userTopicEntity;
  }

  public void assignUser(UserEntity user) {
    this.user = user;
  }
}
