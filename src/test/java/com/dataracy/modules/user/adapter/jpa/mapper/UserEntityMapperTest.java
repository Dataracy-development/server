/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.user.adapter.jpa.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.entity.UserTopicEntity;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;

@DisplayName("UserEntityMapper 테스트")
class UserEntityMapperTest {

  @Test
  @DisplayName("Entity를 Domain으로 변환 성공")
  void toDomain_ShouldConvertEntityToDomain() {
    // Given
    UserEntity entity =
        UserEntity.of(
            ProviderType.GOOGLE,
            "google123",
            RoleType.ROLE_USER,
            "test@example.com",
            "password",
            "testUser",
            1L,
            1L,
            1L,
            "profile.jpg",
            "Introduction",
            true,
            false);

    UserTopicEntity userTopicEntity1 = UserTopicEntity.of(entity, 1L);
    UserTopicEntity userTopicEntity2 = UserTopicEntity.of(entity, 2L);
    entity.addUserTopic(userTopicEntity1);
    entity.addUserTopic(userTopicEntity2);

    // When
    User domain = UserEntityMapper.toDomain(entity);

    // Then
    assertAll(
        () -> assertThat(domain).isNotNull(),
        () -> assertThat(domain.getId()).isNull(), // ID는 설정되지 않음
        () -> assertThat(domain.getProvider()).isEqualTo(ProviderType.GOOGLE),
        () -> assertThat(domain.getProviderId()).isEqualTo("google123"),
        () -> assertThat(domain.getRole()).isEqualTo(RoleType.ROLE_USER),
        () -> assertThat(domain.getEmail()).isEqualTo("test@example.com"),
        () -> assertThat(domain.getNickname()).isEqualTo("testUser"),
        () -> assertThat(domain.getTopicIds()).containsExactlyInAnyOrder(1L, 2L));
  }

  @Test
  @DisplayName("Entity가 null일 때 Domain 변환 시 null 반환")
  void toDomain_WithNullEntity_ShouldReturnNull() {
    // When
    User domain = UserEntityMapper.toDomain(null);

    // Then
    assertThat(domain).isNull();
  }

  @Test
  @DisplayName("Domain을 Entity로 변환 성공")
  void toEntity_ShouldConvertDomainToEntity() {
    // Given
    User domain =
        User.builder()
            .provider(ProviderType.GOOGLE)
            .providerId("google123")
            .role(RoleType.ROLE_USER)
            .email("test@example.com")
            .password("password")
            .nickname("testUser")
            .authorLevelId(1L)
            .occupationId(1L)
            .visitSourceId(1L)
            .profileImageUrl("profile.jpg")
            .introductionText("Introduction")
            .isAdTermsAgreed(true)
            .isDeleted(false)
            .topicIds(List.of(1L, 2L))
            .build();

    // When
    UserEntity entity = UserEntityMapper.toEntity(domain);

    // Then
    assertAll(
        () -> assertThat(entity).isNotNull(),
        () -> assertThat(entity.getProvider()).isEqualTo(ProviderType.GOOGLE),
        () -> assertThat(entity.getProviderId()).isEqualTo("google123"),
        () -> assertThat(entity.getRole()).isEqualTo(RoleType.ROLE_USER),
        () -> assertThat(entity.getEmail()).isEqualTo("test@example.com"),
        () -> assertThat(entity.getNickname()).isEqualTo("testUser"),
        () -> assertThat(entity.getUserTopicEntities()).hasSize(2));
  }

  @Test
  @DisplayName("Domain이 null일 때 Entity 변환 시 null 반환")
  void toEntity_WithNullDomain_ShouldReturnNull() {
    // When
    UserEntity entity = UserEntityMapper.toEntity(null);

    // Then
    assertThat(entity).isNull();
  }

  @Test
  @DisplayName("Domain에 토픽 ID가 null일 때 Entity 변환 성공")
  void toEntity_WithNullTopicIds_ShouldConvertSuccessfully() {
    // Given
    User domain =
        User.builder()
            .provider(ProviderType.GOOGLE)
            .providerId("google123")
            .role(RoleType.ROLE_USER)
            .email("test@example.com")
            .password("password")
            .nickname("testUser")
            .authorLevelId(1L)
            .occupationId(1L)
            .visitSourceId(1L)
            .profileImageUrl("profile.jpg")
            .introductionText("Introduction")
            .isAdTermsAgreed(true)
            .isDeleted(false)
            .topicIds(null)
            .build();

    // When
    UserEntity entity = UserEntityMapper.toEntity(domain);

    // Then
    assertAll(
        () -> assertThat(entity).isNotNull(),
        () -> assertThat(entity.getUserTopicEntities()).isEmpty());
  }
}
