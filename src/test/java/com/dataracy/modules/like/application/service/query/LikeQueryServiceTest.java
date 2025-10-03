/*
 * Copyright (c) 2024 Dataracy
 * Licensed under the MIT License.
 */
package com.dataracy.modules.like.application.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.dataracy.modules.like.application.port.out.query.ReadLikePort;
import com.dataracy.modules.like.application.port.out.validate.ValidateLikePort;
import com.dataracy.modules.like.domain.enums.TargetType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LikeQueryServiceTest {

  @Mock private ReadLikePort readLikePort;

  @Mock private ValidateLikePort validateLikePort;

  @InjectMocks private LikeQueryService service;

  @Nested
  @DisplayName("사용자가 타겟을 좋아요했는지 확인")
  class HasUserLikedTarget {

    @Test
    @DisplayName("validatePort가 true 반환 → true 반환")
    void returnTrueWhenValidatePortReturnsTrue() {
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
    @DisplayName("validatePort가 false 반환 → false 반환")
    void returnFalseWhenValidatePortReturnsFalse() {
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
  }

  @Nested
  @DisplayName("좋아요한 타겟 ID 조회")
  class FindLikedTargetIds {

    @Test
    @DisplayName("readPort 위임 확인")
    void delegateToReadPort() {
      // given
      Long userId = 2L;
      List<Long> input = List.of(1L, 2L, 3L);
      List<Long> expected = List.of(2L, 3L);
      given(readLikePort.findLikedTargetIds(userId, input, TargetType.PROJECT))
          .willReturn(expected);

      // when
      List<Long> result = service.findLikedTargetIds(userId, input, TargetType.PROJECT);

      // then
      assertThat(result).containsExactly(2L, 3L);
      then(readLikePort).should().findLikedTargetIds(userId, input, TargetType.PROJECT);
    }
  }
}
