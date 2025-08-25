package com.dataracy.modules.user.adapter.jpa.impl.command;

import com.dataracy.modules.user.adapter.jpa.entity.UserEntity;
import com.dataracy.modules.user.adapter.jpa.repository.UserJpaRepository;
import com.dataracy.modules.user.domain.enums.ProviderType;
import com.dataracy.modules.user.domain.enums.RoleType;
import com.dataracy.modules.user.domain.exception.UserException;
import com.dataracy.modules.user.domain.model.User;
import com.dataracy.modules.user.domain.status.UserErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandDbAdapterTest {

    @Mock UserJpaRepository userJpaRepository;
    @InjectMocks UserCommandDbAdapter adapter;

    private UserEntity entity() {
        return UserEntity.of(
                ProviderType.LOCAL, "pid", RoleType.ROLE_USER,
                "u@test.com", "pw", "nick", 1L,
                null, null, "img.png", "intro", true, false
        );
    }

    private User domain() {
        return User.of(
                1L, ProviderType.LOCAL, "pid", RoleType.ROLE_USER,
                "u@test.com", "pw", "nick", 1L,
                null, null, null, "img.png", "intro", true, false
        );
    }

    @Test
    @DisplayName("saveUser: 정상 저장")
    void saveUser_success() {
        given(userJpaRepository.save(any(UserEntity.class))).willReturn(entity());

        User result = adapter.saveUser(domain());

        assertThat(result.getEmail()).isEqualTo("u@test.com");
    }

    @Test
    @DisplayName("changePassword: 정상 동작")
    void changePassword_success() {
        UserEntity e = entity();
        given(userJpaRepository.findById(1L)).willReturn(Optional.of(e));

        adapter.changePassword(1L, "new");

        assertThat(e.getPassword()).isEqualTo("new");
    }

    @Test
    @DisplayName("changePassword: 없는 사용자 → 예외")
    void changePassword_notFound() {
        given(userJpaRepository.findById(1L)).willReturn(Optional.empty());

        UserException ex = catchThrowableOfType(() -> adapter.changePassword(1L, "x"), UserException.class);

        assertThat(ex.getErrorCode()).isEqualTo(UserErrorStatus.NOT_FOUND_USER);
    }

    @Test
    @DisplayName("withdrawalUser: 정상 호출")
    void withdrawalUser() {
        adapter.withdrawalUser(1L);
        then(userJpaRepository).should().withdrawalUser(1L);
    }
}

