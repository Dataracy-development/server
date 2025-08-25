package com.dataracy.modules.like.application.service.query;

import com.dataracy.modules.like.application.port.out.query.ReadLikePort;
import com.dataracy.modules.like.application.port.out.validate.ValidateLikePort;
import com.dataracy.modules.like.domain.enums.TargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LikeQueryServiceTest {

    @Mock ReadLikePort readLikePort;
    @Mock ValidateLikePort validateLikePort;

    @InjectMocks LikeQueryService service;

    @Test
    @DisplayName("hasUserLikedTarget_should_return_true_when_validate_port_returns_true")
    void hasUserLikedTarget_should_return_true_when_validate_port_returns_true() {
        // given
        Long userId = 1L;
        Long targetId = 10L;
        TargetType targetType = TargetType.PROJECT;
        given(validateLikePort.isLikedTarget(userId, targetId, targetType)).willReturn(true);

        // when
        boolean result = service.hasUserLikedTarget(userId, targetId, targetType);

        // then
        assertThat(result).isTrue();
        then(validateLikePort).should().isLikedTarget(userId, targetId, targetType);
    }

    @Test
    @DisplayName("hasUserLikedTarget_should_return_false_when_validate_port_returns_false")
    void hasUserLikedTarget_should_return_false_when_validate_port_returns_false() {
        // given
        Long userId = 1L;
        Long targetId = 10L;
        TargetType targetType = TargetType.COMMENT;
        given(validateLikePort.isLikedTarget(userId, targetId, targetType)).willReturn(false);

        // when
        boolean result = service.hasUserLikedTarget(userId, targetId, targetType);

        // then
        assertThat(result).isFalse();
        then(validateLikePort).should().isLikedTarget(userId, targetId, targetType);
    }

    @Test
    @DisplayName("findLikedTargetIds_should_delegate_to_read_port")
    void findLikedTargetIds_should_delegate_to_read_port() {
        // given
        Long userId = 2L;
        List<Long> input = List.of(1L, 2L, 3L);
        List<Long> expected = List.of(2L, 3L);
        given(readLikePort.findLikedTargetIds(userId, input, TargetType.PROJECT)).willReturn(expected);

        // when
        List<Long> result = service.findLikedTargetIds(userId, input, TargetType.PROJECT);

        // then
        assertThat(result).containsExactly(2L, 3L);
        then(readLikePort).should().findLikedTargetIds(userId, input, TargetType.PROJECT);
    }
}
