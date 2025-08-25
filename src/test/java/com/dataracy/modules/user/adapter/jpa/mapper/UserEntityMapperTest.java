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
    @DisplayName("toEntity: Domain → Entity 변환 성공")
    void toEntity_success() {
        UserEntity entity = UserEntityMapper.toEntity(domain());

        assertThat(entity.getEmail()).isEqualTo("u@test.com");
        assertThat(entity.getUserTopicEntities()).hasSize(1);
    }

    @Test
    @DisplayName("toDomain: Entity → Domain 변환 성공")
    void toDomain_success() {
        UserEntity entity = UserEntityMapper.toEntity(domain());

        User user = UserEntityMapper.toDomain(entity);

        assertThat(user.getNickname()).isEqualTo("nick");
        assertThat(user.getTopicIds()).contains(10L);
    }

    @Test
    @DisplayName("null 입력 → null 반환")
    void nullInput_returnsNull() {
        assertThat(UserEntityMapper.toDomain(null)).isNull();
        assertThat(UserEntityMapper.toEntity(null)).isNull();
    }
}

