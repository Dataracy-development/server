package com.dataracy.modules.user.adapter.jpa.mapper;

import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityMapperTest {

    private User domain() {
        return User.of(
                1L, ProviderType.LOCAL, "pid", RoleType.ROLE_USER,
                "u@test.com", "pw", "nick", 1L,
                null, List.of(10L), null,
                "img.png", "intro", true, false
        );
    }

    @Test
    @DisplayName("도메인 → 엔티티 변환 성공")
    void toEntitySuccess() {
        UserEntity entity = UserEntityMapper.toEntity(domain());

        assertThat(entity.getEmail()).isEqualTo("u@test.com");
        assertThat(entity.getUserTopicEntities()).hasSize(1);
    }

    @Test
    @DisplayName("엔티티 → 도메인 변환 성공")
    void toDomainSuccess() {
        UserEntity entity = UserEntityMapper.toEntity(domain());

        User user = UserEntityMapper.toDomain(entity);

        assertThat(user.getNickname()).isEqualTo("nick");
        assertThat(user.getTopicIds()).contains(10L);
    }

    @Test
    @DisplayName("null 입력 시 null 반환")
    void nullInputReturnsNull() {
        assertThat(UserEntityMapper.toDomain(null)).isNull();
        assertThat(UserEntityMapper.toEntity(null)).isNull();
    }
}
