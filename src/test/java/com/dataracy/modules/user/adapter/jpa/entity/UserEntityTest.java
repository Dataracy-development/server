package com.dataracy.modules.user.adapter.jpa.entity;

import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserEntityTest {

    private UserEntity createUser() {
        return UserEntity.of(
                ProviderType.LOCAL,
                "provider-123",
                RoleType.ROLE_USER,
                "user@test.com",
                "encodedPw",
                "nickname",
                1L,
                2L,
                3L,
                "img.png",
                "intro text",
                true,
                false
        );
    }

    @Test
    @DisplayName("of: 정적 팩토리 메서드로 UserEntity 생성 성공")
    void ofSuccess() {
        // when
        UserEntity user = createUser();

        // then
        assertThat(user.getProvider()).isEqualTo(ProviderType.LOCAL);
        assertThat(user.getProviderId()).isEqualTo("provider-123");
        assertThat(user.getRole()).isEqualTo(RoleType.ROLE_USER);
        assertThat(user.getEmail()).isEqualTo("user@test.com");
        assertThat(user.getPassword()).isEqualTo("encodedPw");
        assertThat(user.getNickname()).isEqualTo("nickname");
        assertThat(user.getAuthorLevelId()).isEqualTo(1L);
        assertThat(user.getOccupationId()).isEqualTo(2L);
        assertThat(user.getVisitSourceId()).isEqualTo(3L);
        assertThat(user.getProfileImageUrl()).isEqualTo("img.png");
        assertThat(user.getIntroductionText()).isEqualTo("intro text");
        assertThat(user.isAdTermsAgreed()).isTrue();
        assertThat(user.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("changePassword: 비밀번호 변경 성공")
    void changePasswordSuccess() {
        // given
        UserEntity user = createUser();

        // when
        user.changePassword("newEncodedPw");

        // then
        assertThat(user.getPassword()).isEqualTo("newEncodedPw");
    }

    @Test
    @DisplayName("addUserTopic: 관심 주제 추가 시 리스트에 포함되고 양방향 연관관계가 설정됨")
    void addUserTopic_success() {
        // given
        UserEntity user = createUser();
        UserTopicEntity topicEntity = UserTopicEntity.of(user, 100L);

        // when
        user.addUserTopic(topicEntity);

        // then
        assertThat(user.getUserTopicEntities()).contains(topicEntity);
        assertThat(topicEntity.getUser()).isEqualTo(user);
        assertThat(topicEntity.getTopicId()).isEqualTo(100L);
    }
}
