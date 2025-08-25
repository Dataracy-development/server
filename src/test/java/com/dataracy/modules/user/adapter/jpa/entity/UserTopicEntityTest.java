package com.dataracy.modules.user.adapter.jpa.entity;

import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTopicEntityTest {

    private UserEntity user() {
        return UserEntity.of(
                ProviderType.LOCAL, "pid", RoleType.ROLE_USER,
                "u@test.com", "pw", "nick", 1L,
                null, null, "img.png", "intro", true, false
        );
    }

    @Test
    @DisplayName("of: UserTopicEntity 생성 및 연결 성공")
    void of_success() {
        UserEntity user = user();
        UserTopicEntity topic = UserTopicEntity.of(user, 100L);

        assertThat(topic.getUser()).isEqualTo(user);
        assertThat(topic.getTopicId()).isEqualTo(100L);
    }
}
