package com.dataracy.modules.like.adapter.jpa.impl;

import com.dataracy.modules.like.adapter.jpa.entity.LikeEntity;
import com.dataracy.modules.like.adapter.jpa.mapper.LikeEntityMapper;
import com.dataracy.modules.like.adapter.jpa.repository.LikeJpaRepository;
import com.dataracy.modules.like.domain.enums.TargetType;
import com.dataracy.modules.like.domain.exception.LikeException;
import com.dataracy.modules.like.domain.model.Like;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LikeCommandAdapterTest {

    @Mock LikeJpaRepository likeJpaRepository;

    @InjectMocks LikeCommandAdapter adapter;

    @Captor ArgumentCaptor<LikeEntity> entityCaptor;

    @Test
    @DisplayName("save_should_convert_domain_and_persist")
    void save_should_convert_domain_and_persist() {
        // given
        Like like = Like.of(null, 88L, TargetType.PROJECT, 100L);
        given(likeJpaRepository.save(any(LikeEntity.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        adapter.save(like);

        // then
        then(likeJpaRepository).should().save(entityCaptor.capture());
        LikeEntity saved = entityCaptor.getValue();
        assertThat(saved.getTargetId()).isEqualTo(88L);
        assertThat(saved.getTargetType()).isEqualTo(TargetType.PROJECT);
        assertThat(saved.getUserId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("cancelLike_should_delete_when_entity_exists")
    void cancelLike_should_delete_when_entity_exists() {
        // given
        Long userId = 7L;
        Long targetId = 3L;
        TargetType targetType = TargetType.COMMENT;
        LikeEntity existing = LikeEntity.of(targetId, targetType, userId);
        given(likeJpaRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType))
                .willReturn(Optional.of(existing));

        // when
        adapter.cancelLike(userId, targetId, targetType);

        // then
        then(likeJpaRepository).should().delete(existing);
    }

    @Test
    @DisplayName("cancelLike_should_throw_when_entity_not_found")
    void cancelLike_should_throw_when_entity_not_found() {
        // given
        Long userId = 7L;
        Long targetId = 3L;
        TargetType targetType = TargetType.PROJECT;
        given(likeJpaRepository.findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType))
                .willReturn(Optional.empty());

        // when
        LikeException ex = catchThrowableOfType(() -> adapter.cancelLike(userId, targetId, targetType), LikeException.class);

        // then
        assertThat(ex).isNotNull();
        then(likeJpaRepository).should(never()).delete(any());
    }
}
