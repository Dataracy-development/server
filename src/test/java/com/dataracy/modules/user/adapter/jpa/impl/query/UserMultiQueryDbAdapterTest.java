package com.dataracy.modules.user.adapter.jpa.impl.query;

import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserMultiQueryDbAdapterTest {

    @Mock UserJpaRepository userJpaRepository;
    @InjectMocks UserMultiQueryDbAdapter adapter;

    private UserEntity entity() {
        return UserEntity.of(
                ProviderType.LOCAL, "pid", RoleType.ROLE_USER,
                "u@test.com", "pw", "nick", null,
                null, null, null, "intro", true, false
        );
    }

    @Test
    @DisplayName("findUsernamesByIds: 정상 조회")
    void findUsernamesByIds() {
        given(userJpaRepository.findAllById(anyList())).willReturn(List.of(entity()));

        Map<Long, String> map = adapter.findUsernamesByIds(List.of(1L));

        assertThat(map).isNotEmpty();
    }

    @Test
    @DisplayName("findUserThumbnailsByIds: null 이미지 → 빈 문자열")
    void findUserThumbnailsByIds() {
        UserEntity e = entity();
        given(userJpaRepository.findAllById(anyList())).willReturn(List.of(e));

        Map<Long, String> map = adapter.findUserThumbnailsByIds(List.of(1L));

        assertThat(map.values()).contains("");
    }

    @Test
    @DisplayName("findUserAuthorLevelIds: null → 기본값 1")
    void findUserAuthorLevelIds() {
        UserEntity e = entity();
        given(userJpaRepository.findAllById(anyList())).willReturn(List.of(e));

        Map<Long, String> map = adapter.findUserAuthorLevelIds(List.of(1L));

        assertThat(map.values()).contains("1");
    }
}

