package com.dataracy.modules.user.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * users, topic 다대다 연결 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "user_topic")
public class UserTopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_topic_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Long topicId;

    public static UserTopicEntity of(UserEntity user, Long topicId) {
        UserTopicEntity userTopicEntity = UserTopicEntity.builder()
                .topicId(topicId)
                .build();
        userTopicEntity.setUser(user);
        return userTopicEntity;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
